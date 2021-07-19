package cantseechess;

import cantseechess.chess.*;
import cantseechess.storage.RatingStorage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
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

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class BotListener extends ListenerAdapter {
    private final RatingStorage ratings;
    private final HashMap<Guild, ArrayDeque<String>> availableChannels = new HashMap<>();
    private final HashMap<String, Player> currentPlayers = new HashMap<>();
    private final HashMap<String, Challenge> challenges = new HashMap<>();
    private final ArrayList<BoardMessage> boardMessages = new ArrayList<>(); //TODO store this :(
    private final Timer challengeTimeout = new Timer();
    private static final String commandPrefix = "!";

    public BotListener(RatingStorage ratings) {
        this.ratings = ratings;
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

    private void endGame(Player player, ChessGame.EndState endState, Guild guild) {
        if (endState == ChessGame.EndState.NotOver) {
            return;
        }

        // this looks so gnarly
        var channel = guild.getTextChannelById(player.getChannel());
        assert channel != null;
        channel.putPermissionOverride(guild.retrieveMemberById(player.getId()).complete())
                .setDeny(Permission.MESSAGE_WRITE).queue();
        channel.putPermissionOverride(guild.retrieveMemberById(player.getOpponent().getId()).complete())
                .setDeny(Permission.MESSAGE_WRITE).queue();
        // return channel
        availableChannels.get(guild).add(player.getChannel());
        // TODO: better cleanup, rating adjustment!
        if (endState == ChessGame.EndState.Draw) {
            ratings.addGame(player, new Rating.GameEntry(player.getOpponent().getRating(), 0.5));
            ratings.addGame(player.getOpponent(), new Rating.GameEntry(player.getRating(), 0.5));
        } else if (endState == ChessGame.EndState.WhiteWins) {
            ratings.addGame(player.getWhite(), new Rating.GameEntry(player.getBlack().getRating(), 1));
            ratings.addGame(player.getBlack(), new Rating.GameEntry(player.getWhite().getRating(), 0));
        } else {
            ratings.addGame(player.getWhite(), new Rating.GameEntry(player.getBlack().getRating(), 0));
            ratings.addGame(player.getBlack(), new Rating.GameEntry(player.getWhite().getRating(), 1));
        }

        try {
            boardMessages.add(new BoardMessage(channel, player.getCurrentGame().getPGN()));
        } catch (IncorrectFENException | IllegalMoveException e) {
            e.printStackTrace();
        }

        currentPlayers.remove(player.getOpponent().getId());
        player.getOpponent().resetGameInfo();
        currentPlayers.remove(player.getId());
        player.resetGameInfo();
    }

    private String challenge(Guild guild, User challenger, String challengedId, Optional<Color> color, Optional<String> time, Optional<Integer> increment) {
        if (availableChannels.get(guild).isEmpty()) {
            return "no available boards!";
        }

        String gameTimeString = time.orElse("-1s");

        int gameTime = 0;
        for (String s : gameTimeString.split(" ")) {
            int parsed = Integer.parseInt(s.replaceAll("[^0-9.]", ""));
            if (s.contains("m")) {
                gameTime += parsed * 60;
            } else if (s.contains("s")) {
                gameTime += parsed;
            }
        }
        int gameIncrement = increment.orElse(0);
        //Pick a random color if color isn't supplied
        Color challengerColor = color.orElse(Math.round(Math.random()) == 1 ? Color.white : Color.black);

        //var challengedId = parseMention(args[1]);
        if (challengedId != null) {
            System.out.println(challengedId);
            // hopefully valid user mention
            if (challenges.containsKey(challengedId)) {
                return "already challenged!";
            }
            challenges.put(challengedId, new Challenge(challenger.getId(), challengedId, challengerColor, gameTime, gameIncrement));
            challengeTimeout.schedule(new CancelChallengeTask(challenges, challengedId), 1000 * 120);
            return "challenge created with " + "<@!" + challengedId + ">";
        } else return "unable to find user";
    }

    private String accept(Guild guild, User sender) {
        System.out.println(sender.getId());
        var challenge = challenges.get(sender.getId());
        if (challenge == null) {
            return "no challenge!";
        }
        // TODO: we should queue up the game when there are no available channels instead of failing
        var channelId = availableChannels.get(guild).poll();
        if (channelId == null) {
            return "no available boards!";
        }
        var boardChannel = guild.getTextChannelById(channelId);
        if (boardChannel == null) {
            throw new NullPointerException("channelId is not a real channel?");
        }
        boardChannel.putPermissionOverride(guild.retrieveMemberById(challenge.challenged).complete())
                .setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE).queue();
        boardChannel.putPermissionOverride(guild.retrieveMemberById(challenge.challenger).complete())
                .setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE).queue();
        var game = challenge.accept();
        var player1 = new Player(game, challenge.challengerColor.other(), challenge.challenged, channelId);
        var player2 = new Player(game, challenge.challengerColor, challenge.challenger, channelId);
        player1.setOpponent(player2);
        player2.setOpponent(player1);
        currentPlayers.put(challenge.challenged, player1);
        currentPlayers.put(challenge.challenger, player2);
        challenges.remove(sender.getId());

        Player white = player1.getWhite();
        Player black = player1.getBlack();
        int whiteRating = ratings.getRating(white.getId()) != null ? (int) Math.round(ratings.getRating(white.getId()).getRating()) : (int) Rating.DEFAULT_RATING;
        int blackRating = ratings.getRating(black.getId()) != null ? (int) Math.round(ratings.getRating(black.getId()).getRating()) : (int) Rating.DEFAULT_RATING;
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("Game Begun!")
                .addField("White", "<@!" + white.getId() + "> " + whiteRating, true)
                .addField("Black", "<@!" + black.getId() + "> " + blackRating, true);
        //boardChannel.sendMessage("game between <@!" + challenge.challenged + "> and <@!" + challenge.challenger + "> begun!").queue();
        boardChannel.sendMessage(builder.build()).queue();
        return "created game in " + boardChannel.getAsMention();
    }

    private String decline(User sender) {
        var challenge = challenges.remove(sender.getId());
        if (challenge == null) {
            return "no challenge to decline!";
        }
        return "challenge declined.";
    }

    private String stats(User sender, Optional<String> target) {
        var rating = ratings.getRating(target.orElse(sender.getId()));
        if (rating != null) {
            return "rating: " + rating.getRating() +
                    " rd: " + rating.getDeviation();
        } else {
            return "unable to find stats";
        }
    }

    private void resign(Guild guild, User sender) {
        var player = currentPlayers.get(sender.getId());
        if (player.isPlayingGame()) {
            endGame(player,
                    player.getColor() == Color.white ?
                            ChessGame.EndState.BlackWins : ChessGame.EndState.WhiteWins,
                    guild);
        }
    }

    private String imp(TextChannel channel, String PGN) {
        try {
            BoardMessage msg = new BoardMessage(channel, PGN);
            boardMessages.add(msg);
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
                    var colorOption = Optional.ofNullable(event.getOption("color"));
                    var timeOption = Optional.ofNullable(event.getOption("time"));
                    var incrementOption = Optional.ofNullable(event.getOption("time"));

                    var challengedId = Objects.requireNonNull(event.getOption("user")).getAsUser().getId();
                    var color = colorOption.map(option -> Color.valueOf(option.getAsString().toLowerCase()));
                    var time = timeOption.map(OptionMapping::getAsString);
                    var increment = incrementOption.map(option -> Integer.parseInt(option.getAsString().replaceAll("[^0-9.]", "")));

                    reply = challenge(event.getGuild(), event.getUser(),
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
                var user = userOption.map(option -> option.getAsUser().getId());

                reply = stats(event.getUser(), user);
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
                resign(event.getGuild(), event.getUser());
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
            var args = content.substring(commandPrefix.length()).split(" ");
            String reply = null;
            switch (args[0]) {
                case "challenge":
                    if (args.length >= 4)
                        reply = challenge(event.getGuild(), event.getAuthor(), parseMention(args[1]), Optional.of(Color.valueOf(args[2].toLowerCase())), Optional.of(args[2]), Optional.of(Integer.parseInt(args[4])));
                    else
                        reply = challenge(event.getGuild(), event.getAuthor(), parseMention(args[1]), Optional.empty(), Optional.empty(), Optional.empty());
                    break;
                case "accept":
                    reply = accept(event.getGuild(), event.getAuthor());
                    break;
                case "decline":
                    reply = decline(event.getAuthor());
                    break;
                case "resign":
                    resign(event.getGuild(), event.getAuthor());
                    break;
                case "stats":
                    stats(event.getAuthor(), args.length >= 2 ? Optional.ofNullable(parseMention(args[1])) : Optional.empty());
                    break;
                case "import":
                    StringBuilder PGN = new StringBuilder();
                    for (int i = 1; i < args.length; i++) {
                        PGN.append(args[i]).append(" ");
                    }
                    if (PGN.length() == 0) {
                        event.getChannel().sendMessage("enter the pgn to import").queue();
                        return;
                    }
                    imp(event.getTextChannel(), PGN.toString());
                    break;
            }
            if (reply != null) {
                message.reply(reply).queue();
            }
        } else if (currentPlayers.containsKey(event.getAuthor().getId())) {
            var player = currentPlayers.get(event.getAuthor().getId());
            if (!player.getChannel().equals(event.getChannel().getId())) {
                return;
            }
            try {
                player.makeMove(content);
                event.getMessage().addReaction("U+2705").queue();
            } catch (IllegalMoveException | IllegalArgumentException e) {
                event.getMessage().addReaction("U+26D4").queue();
            }
            var endState = player.isGameOver();
            endGame(player, endState, event.getGuild());
        }
    }

    //Add all of the guilds that the bot is in to availableChannels and add all of their respective boards
    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        availableChannels.put(event.getGuild(), new ArrayDeque<>());
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
        var channels = availableChannels.get(event.getGuild());
        var channel = event.getChannel();
        if (isBoardChannel(channel)) {
            channels.remove(channel.getId());
        }
    }

    //If a text channel updates its name to "board", then add it to the available channels. If they change it from a board to something else, then remove it.
    @Override
    public void onTextChannelUpdateName(@NotNull TextChannelUpdateNameEvent event) {
        if (event.getNewName().startsWith("board") && !availableChannels.get(event.getGuild()).contains(event.getChannel().getId())) {
            setAvailable(event.getGuild(), event.getChannel());
        } else if (!event.getNewName().startsWith("board")) {
            availableChannels.get(event.getGuild()).remove(event.getChannel().getId());
        }
    }

    //Adds channel to the available channels in guild
    private void setAvailable(Guild guild, TextChannel channel) {
        availableChannels.get(guild).add(channel.getId());
    }

    //When a user clicks on a button on a board message, do something accordingly
    @Override
    public void onButtonClick(ButtonClickEvent event) {
        event.deferEdit().queue();
        for (BoardMessage m : boardMessages) {
            if (m.getMessage().equals(event.getMessage())) {
                if (event.getComponentId().equals("Analyze")) {
                    m.doAnalysis = !m.doAnalysis;
                    m.startAnalysis();
                } else {
                    try {
                        m.getClass().getMethod(event.getComponentId().toLowerCase()).invoke(m);
                    } catch (NoSuchMethodException |
                            InvocationTargetException |
                            IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
        ratings.shutdown();
    }
}
