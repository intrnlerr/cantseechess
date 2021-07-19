package cantseechess;

import cantseechess.chess.*;
import cantseechess.storage.RatingStorage;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.text.update.TextChannelUpdateNameEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;

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

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        var content = event.getMessage().getContentRaw();
        System.out.println(content);
        if (content.startsWith(commandPrefix)) {
            var args = content.substring(commandPrefix.length()).split(" ");
            if (args[0].equals("challenge")) {

                if (availableChannels.get(event.getGuild()).isEmpty()) {
                    event.getChannel().sendMessage("no available boards!").queue();
                    return;
                }

                var challengedId = parseMention(args[1]);
                if (challengedId != null) {
                    System.out.println(challengedId);
                    // hopefully valid user mention
                    if (challenges.containsKey(challengedId)) {
                        event.getChannel().sendMessage("already challenged!").queue();
                        return;
                    }
                    challenges.put(challengedId, new Challenge(event.getAuthor().getId(), challengedId));
                    challengeTimeout.schedule(new CancelChallengeTask(challenges, challengedId), 1000 * 120);
                    var action = event.getChannel().sendMessage("challenge created...");
                    action.queue();
                }
            } else if (args[0].equals("accept")) {
                var challenge = challenges.get(event.getAuthor().getId());
                if (challenge == null) {
                    event.getChannel().sendMessage("no challenge!").queue();
                    return;
                }
                // TODO: we should queue up the game when there are no available channels instead of failing
                var channelId = availableChannels.get(event.getGuild()).poll();
                if (channelId == null) {
                    event.getChannel().sendMessage("no available boards!").queue();
                    return;
                }
                var guild = event.getGuild();
                var channel = guild.getTextChannelById(channelId);
                if (channel == null) {
                    throw new NullPointerException("channelId is not a real channel?");
                }
                channel.putPermissionOverride(guild.retrieveMemberById(challenge.challenged).complete())
                        .setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE).queue();
                channel.putPermissionOverride(guild.retrieveMemberById(challenge.challenger).complete())
                        .setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE).queue();
                var game = challenge.accept();
                var player1 = new Player(game, Color.white, challenge.challenged, channelId);
                var player2 = new Player(game, Color.black, challenge.challenger, channelId);
                player1.setOpponent(player2);
                player2.setOpponent(player1);
                currentPlayers.put(challenge.challenged, player1);
                currentPlayers.put(challenge.challenger, player2);
                challenges.remove(event.getAuthor().getId());
                channel.sendMessage("game between <@!" + challenge.challenged + "> and <@!" + challenge.challenger + "> begun!").queue();
            } else if (args[0].equals("decline")) {
                var challenge = challenges.remove(event.getAuthor().getId());
                if (challenge == null) {
                    event.getChannel().sendMessage("no challenge to decline!").queue();
                    return;
                }
                event.getChannel().sendMessage("challenge declined.").queue();
            } else if (args[0].equals("sp")) {
                currentPlayers.put(event.getAuthor().getId(), new SelfPlayer(event.getAuthor().getId(), event.getChannel().getId()));
            } else if (args[0].equals("help")) {
                //TODO help command
            } else if (args[0].equals("resign")) {
                var player = currentPlayers.get(event.getAuthor().getId());
                if (player.isPlayingGame()) {
                    endGame(player,
                            player.getColor() == Color.white ?
                                    ChessGame.EndState.BlackWins : ChessGame.EndState.WhiteWins,
                            event.getGuild());
                }
            } else if (args[0].equals("stats")) {
                var target = event.getAuthor().getId();
                if (args.length > 1) {
                    target = parseMention(args[1]);
                }
                var rating = ratings.getRating(target);
                if (rating != null) {
                    event.getChannel().sendMessage("rating: " + rating.getRating() +
                            " rd: " + rating.getDeviation()).queue();
                }
            } else if (args[0].equals("import")) {
                StringBuilder PGN = new StringBuilder();
                if (args.length > 1) {
                    for (int i = 1; i < args.length - 1; i++) PGN.append(args[i] + " ");
                    PGN.append(args[args.length - 1]);
                } else {
                    event.getChannel().sendMessage("Please enter the PGN you would like to import").queue();
                    return;
                }
                try {
                    BoardMessage message = new BoardMessage(event.getMessage().getTextChannel(), PGN.toString());
                    boardMessages.add(message);
                } catch (IncorrectFENException | IllegalMoveException e) {
                    event.getChannel().sendMessage("Please enter a correct PGN").queue();
                    e.printStackTrace();
                }
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
        if (isBoardChannel(channel) && channels.contains(channel)) {
            channels.remove(channel.getId());
        }
    }

    //If a text channel updates its name to "board", then add it to the available channels. If they change it from a board to something else, then remove it.
    @Override
    public void onTextChannelUpdateName(@NotNull TextChannelUpdateNameEvent event) {
        if (event.getNewName().startsWith("board") && !availableChannels.get(event.getGuild()).contains(event.getChannel().getId())) {
            setAvailable(event.getGuild(), event.getChannel());
        } else if (!event.getNewName().startsWith("board") && availableChannels.get(event.getGuild()).contains(event.getChannel().getId())) {
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
