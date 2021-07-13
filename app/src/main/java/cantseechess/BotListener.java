package cantseechess;

import cantseechess.chess.ChessGame;
import cantseechess.chess.Color;
import cantseechess.chess.IllegalMoveException;
import cantseechess.chess.Rating;
import cantseechess.storage.RatingStorage;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.HashMap;

public class BotListener extends ListenerAdapter {
    private final RatingStorage ratings;
    private final ArrayDeque<String> availableChannels = new ArrayDeque<>();
    private final HashMap<String, Player> currentPlayers = new HashMap<>();
    private final HashMap<String, Challenge> challenges = new HashMap<>();

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

    private void endGame(Player player, ChessGame.EndState endState) {
        if (endState == ChessGame.EndState.NotOver) {
            return;
        }
        // return channel
        availableChannels.push(player.getChannel());
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

        currentPlayers.remove(player.getOpponent().getId());
        player.getOpponent().resetGameInfo();
        currentPlayers.remove(player.getId());
        player.resetGameInfo();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        var content = event.getMessage().getContentRaw();
        System.out.println(content);
        if (content.startsWith("!")) {
            var args = content.split(" ");
            if (args[0].equals("!challenge")) {
                var challengedId = parseMention(args[1]);
                if (challengedId != null) {
                    System.out.println(challengedId);
                    // hopefully valid user mention
                    if (challenges.containsKey(challengedId)) {
                        event.getChannel().sendMessage("already challenged!").queue();
                        return;
                    }
                    challenges.put(challengedId, new Challenge(event.getAuthor().getId(), challengedId));
                    var action = event.getChannel().sendMessage("challenge created...");
                    action.queue();
                }
            } else if (args[0].equals("!accept")) {
                var challenge = challenges.get(event.getAuthor().getId());
                if (challenge == null) {
                    event.getChannel().sendMessage("no challenge!").queue();
                    return;
                }
                // TODO: we should queue up the game when there are no available channels instead of failing
                var assignedChannel = availableChannels.poll();
                if (assignedChannel == null) {
                    event.getChannel().sendMessage("no available boards!").queue();
                    return;
                }
                var game = challenge.accept();
                var player1 = new Player(game, Color.white, challenge.challenged, assignedChannel);
                var player2 = new Player(game, Color.black, challenge.challenger, assignedChannel);
                player1.setOpponent(player2);
                player2.setOpponent(player1);
                currentPlayers.put(challenge.challenged, player1);
                currentPlayers.put(challenge.challenger, player2);
                challenges.remove(event.getAuthor().getId());
                event.getGuild().getTextChannelById(assignedChannel)
                        .sendMessage("game between <@!" + challenge.challenged + "> and <@!" + challenge.challenger + "> begun!").queue();
            } else if (args[0].equals("!decline")) {
                var challenge = challenges.remove(event.getAuthor().getId());
                if (challenge == null) {
                    event.getChannel().sendMessage("no challenge to decline!").queue();
                    return;
                }
                event.getChannel().sendMessage("challenge declined.").queue();
            } else if (args[0].equals("!sp")) {
                currentPlayers.put(event.getAuthor().getId(), new SelfPlayer(event.getAuthor().getId(), event.getChannel().getId()));
            } else if (args[0].equals("!help")) {
                //TODO help command
            } else if (args[0].equals("!resign")) {
                var player = currentPlayers.get(event.getAuthor().getId());
                if (player.isPlayingGame()) {
                    endGame(player,
                            player.getColor() == Color.white ?
                                    ChessGame.EndState.BlackWins : ChessGame.EndState.WhiteWins);
                }
            } else if (args[0].equals("!stats")) {
                var target = event.getAuthor().getId();
                if (args.length > 1) {
                    target = parseMention(args[1]);
                }
                var rating = ratings.getRating(target);
                if (rating != null) {
                    event.getChannel().sendMessage("rating: " + rating.getRating() +
                            " rd: " + rating.getDeviation()).queue();
                }
            }
        } else if (currentPlayers.containsKey(event.getAuthor().getId())) {
            var player = currentPlayers.get(event.getAuthor().getId());
            if (!player.getChannel().equals(event.getChannel().getId())) {
                return;
            }
            try {
                player.makeMove(content);
            } catch (IllegalMoveException e) {
                event.getChannel().sendMessage(e.getMessage()).queue();
            }
            var endState = player.isGameOver();
            endGame(player, endState);
        }
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        for (var channel : event.getGuild().getTextChannels()) {
            if (channel.canTalk() && channel.getName().startsWith("board")) {
                availableChannels.add(channel.getId());
            }
        }
        System.out.println("found " + availableChannels.size() + " boards");
        // TODO: what if there are no boards?
    }

    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
        ratings.shutdown();
    }
}
