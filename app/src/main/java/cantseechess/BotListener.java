package cantseechess;

import cantseechess.chess.Color;
import cantseechess.chess.IllegalMoveException;
import cantseechess.chess.IncorrectFENException;
import cantseechess.storage.RatingStorage;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.text.update.TextChannelUpdateNameEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.*;

public class BotListener extends ListenerAdapter {
    private final RatingStorage ratings;
    private final GameManager gameManager;
    private final HashMap<String, Challenge> challenges = new HashMap<>();
    private final BoardMessageManager boardMessageManager;
    private final Timer challengeTimeout = new Timer();
    private static final String commandPrefix = "!";

    public BotListener(RatingStorage ratings, String stockfishPath, String emojiGuild) {
        this.ratings = ratings;
        boardMessageManager = new BoardMessageManager(stockfishPath, emojiGuild);
        this.gameManager = new GameManager(ratings, boardMessageManager);
    }

    // gets user id from mention
    private String parseMention(String mention) {
        if (mention.matches("<@!?[0-9]+>")) {
            if (mention.charAt(2) == '!') {
                return mention.substring(3, mention.length() - 1);
            } else {
                return mention.substring(2, mention.length() - 1);
            }
        }
        return null;
    }

    private String parseChallenge(User challenger, ArgIterator args) {
        if (!args.hasNext()) {
            return "Mention the user you want to challenge";
        }
        var challengedId = parseMention(args.next().name);
        if (challengedId == null) {
            return "Not a mention";
        }
        // time is in seconds
        int time = 10 * (60);
        int increment = 0;
        Color challengerColor = Math.random() < 0.5 ? Color.white : Color.black;
        String startFen = null;
        for (var arg : args) {
            if (arg.value != null) {
                if (arg.name.equals("color") || arg.name.equals("c")) {
                    challengerColor = arg.value.equals("w") ? Color.white : Color.black;
                } else if (arg.name.equals("fen")) {
                    startFen = arg.value;
                }
            } else if (arg.name.equals("aswhite")) {
                challengerColor = Color.white;
            } else if (arg.name.equals("asblack")) {
                challengerColor = Color.black;
            } else if (arg.name.matches("\\A[0-9]+\\+[0-9]+")) {
                var timeControl = arg.name.split("\\+");
                try {
                    time = Integer.parseInt(timeControl[0]);
                    if (time == 0) {
                        return "wierd";
                    }
                    increment = Integer.parseInt(timeControl[0]);
                } catch (NumberFormatException e) {
                    return "Invalid time control";
                }
            }
        }

        if (challenges.containsKey(challengedId)) {
            return "already challenged!";
        }
        challenges.put(challengedId, new Challenge(challenger.getId(), challengedId, challengerColor, startFen, time, increment));
        challengeTimeout.schedule(new CancelChallengeTask(challenges, challengedId), 1000 * 120);
        return "challenge created with " + "<@!" + challengedId + ">";
    }

    private String challenge(User challenger, String challengedId, @Nonnull Color challengerColor, String time, int increment) {
        int gameTime = 0;
        for (String s : time.split(" ")) {
            int parsed = Integer.parseInt(s.replaceAll("[^0-9.]", ""));
            if (s.contains("m")) {
                gameTime += parsed * 60;
            } else if (s.contains("s")) {
                gameTime += parsed;
            }
        }

        //var challengedId = parseMention(args[1]);
        if (challengedId != null) {
            System.out.println(challengedId);
            // hopefully valid user mention
            if (challenges.containsKey(challengedId)) {
                return "already challenged!";
            }
            challenges.put(challengedId, new Challenge(challenger.getId(), challengedId, challengerColor, null, gameTime, increment));
            challengeTimeout.schedule(new CancelChallengeTask(challenges, challengedId), 1000 * 120);
            return "challenge created with " + "<@!" + challengedId + ">";
        } else return "unable to find user";
    }

    private String accept(Guild guild, User sender) {
        var challenge = challenges.get(sender.getId());
        if (challenge == null) {
            return "no challenge found";
        }
        gameManager.startGame(guild, challenge);
        challenges.remove(sender.getId());
        return "queued game";
    }

    private String decline(User sender) {
        var challenge = challenges.remove(sender.getId());
        if (challenge == null) {
            return "no challenge to decline!";
        }
        return "challenge declined.";
    }

    private String stats(long target) {
        var rating = ratings.getRating(target);
        if (rating != null) {
            return "rating: " + rating.getRating() +
                    " rd: " + rating.getDeviation();
        } else {
            return "unable to find stats";
        }
    }

    private void resign(AbstractChannel channel, User sender) {
        gameManager.resignGame(channel.getIdLong(), sender);
    }

    private void draw(AbstractChannel channel, User sender) {
        gameManager.sendDrawRequest(channel, sender);
    }

    private String imp(TextChannel channel, String PGN) {
        try {
            boardMessageManager.add(channel, PGN, "Imported Game");
            return "importing game...";
        } catch (IncorrectFENException | IllegalMoveException e) {
            return "Please enter a correct PGN";
        }
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        String reply = "OK";
        switch (event.getName()) {
            case "challenge":
                try {
                    var colorOption = event.getOption("color");
                    var timeOption = Optional.ofNullable(event.getOption("time"));
                    var incrementOption = Optional.ofNullable(event.getOption("time"));

                    var challengedId = Objects.requireNonNull(event.getOption("user")).getAsUser().getId();
                    var color = colorOption == null ?
                            (Math.random() < 0.5 ? Color.white : Color.black) :
                            Color.valueOf(colorOption.getAsString().toLowerCase());
                    var time = timeOption.map(OptionMapping::getAsString).orElse("1s");
                    var increment = incrementOption.map(option -> Integer.parseInt(option.getAsString().replaceAll("[^0-9.]", ""))).orElse(0);

                    reply = challenge(event.getUser(),
                            challengedId,
                            color,
                            time,
                            increment);
                } catch (Exception e) {
                    e.printStackTrace();
                    reply = "incorrect parameters";
                }
                break;

            case "accept":
                reply = accept(event.getGuild(), event.getUser());
                break;
            case "decline":
                reply = decline(event.getUser());
                break;
            case "stats":
                var userOption = Optional.ofNullable(event.getOption("user"));
                var user = userOption.map(option -> option.getAsUser().getIdLong());

                reply = stats(user.orElse(event.getUser().getIdLong()));

                break;
            case "import":
                try {
                    var PGN = Optional.ofNullable(event.getOption("pgn")).map(OptionMapping::getAsString).orElseThrow();
                    reply = imp(event.getTextChannel(), PGN);
                } catch (NoSuchElementException e) {
                    reply = "Enter a PGN";
                }
                break;
            case "resign":
                resign(event.getGuildChannel(), event.getUser());
                reply = "are you sure you want to resign?";
                break;

        }
        event.reply(reply).queue();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        var content = event.getMessage().getContentRaw();
        var message = event.getMessage();
        if (content.startsWith(commandPrefix)) {
            var args = new ArgIterator(content.substring(commandPrefix.length()));
            String reply = null;
            switch (args.next().name) {
                case "challenge":
                    reply = parseChallenge(event.getAuthor(), args);
                    break;
                case "accept":
                    reply = accept(event.getGuild(), event.getAuthor());
                    break;
                case "decline":
                    reply = decline(event.getAuthor());
                    break;
                case "resign":
                    resign(event.getChannel(), event.getAuthor());
                    break;
                case "stats":
                    stats(args.hasNext() ? Long.parseLong(args.next().name) : event.getAuthor().getIdLong());
                    break;
                case "import":
                    StringBuilder PGN = new StringBuilder();
                    while (args.hasNext()) {
                        var arg = args.next();
                        PGN.append(arg.name);
                        if (!arg.isTag()) {
                            PGN.append('=').append(arg.value);
                        }
                        PGN.append(" ");
                    }
                    if (PGN.length() == 0) {
                        event.getChannel().sendMessage("enter the pgn to import").queue();
                        return;
                    }
                    imp(event.getTextChannel(), PGN.toString());
                    break;
                case "draw":
                    draw(event.getTextChannel(), event.getAuthor());
                    break;
            }
            if (reply != null) {
                message.reply(reply).queue();
            }
        } else {
            gameManager.playMove(event.getChannel(), event.getMessage());
        }
    }

    //Add all of the guilds that the bot is in to availableChannels and add all of their respective boards
    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        gameManager.addGuild(event.getGuild());
        for (var channel : event.getGuild().getTextChannels()) {
            if (isBoardChannel(channel)) {
                setAvailable(event.getGuild(), channel);
            }
        }
    }

    //Returns if the bot can send messages in the channel and the channel starts with "board"
    private static boolean isBoardChannel(TextChannel channel) {
        return channel.getName().startsWith("board") && channel.canTalk();
    }

    //Add a board to availableChannels when it's created
    @Override
    public void onTextChannelCreate(@NotNull TextChannelCreateEvent event) {
        if (isBoardChannel(event.getChannel())) {
            setAvailable(event.getGuild(), event.getChannel());
        }
    }

    //Delete a board from availableChannels when it's deleted
    @Override
    public void onTextChannelDelete(@NotNull TextChannelDeleteEvent event) {
        gameManager.dropChannel(event.getGuild(), event.getChannel());
    }

    //If a text channel updates its name to "board", then add it to the available channels. If they change it from a board to something else, then remove it.
    @Override
    public void onTextChannelUpdateName(@NotNull TextChannelUpdateNameEvent event) {
        if (event.getNewName().startsWith("board") && !gameManager.isAvailableBoard(event.getChannel())) {
            setAvailable(event.getGuild(), event.getChannel());
        } else if (!event.getNewName().startsWith("board")) {
            gameManager.dropChannel(event.getGuild(), event.getChannel());
        }
    }

    //Adds channel to the available channels in guild
    private void setAvailable(Guild guild, TextChannel channel) {
        gameManager.addChannel(guild, channel.getIdLong());
    }

    //When a user clicks on a button on a board message, do something accordingly
    @Override
    public void onButtonClick(ButtonClickEvent event) {
        event.deferEdit().queue();
        boardMessageManager.onButtonClick(event.getMessageIdLong(), event.getComponentId());
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        boardMessageManager.setEmojiGuild(event.getJDA());
    }

    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
        ratings.shutdown();
    }
}
