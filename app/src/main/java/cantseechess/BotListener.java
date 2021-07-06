package cantseechess;

import cantseechess.chess.ChessGame;
import cantseechess.chess.Color;
import cantseechess.chess.IllegalMoveException;
import cantseechess.chess.Rating;
import cantseechess.storage.RatingStorage;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class BotListener extends ListenerAdapter {
    private final RatingStorage ratings;
    private final HashMap<String, Player> players = new HashMap<>();
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
                var game = challenge.accept();
                var player1 = new Player(game, Color.white, challenge.challenged);
                var player2 = new Player(game, Color.black, challenge.challenger);
                player1.setOpponent(player2);
                player2.setOpponent(player1);
                players.put(challenge.challenged, player1);
                players.put(challenge.challenger, player2);
                challenges.remove(event.getAuthor().getId());
                event.getChannel().sendMessage("game").queue();
            } else if (args[0].equals("!decline")) {
                var challenge = challenges.remove(event.getAuthor().getId());
                if (challenge == null) {
                    event.getChannel().sendMessage("no challenge to decline!").queue();
                    return;
                }
                event.getChannel().sendMessage("challenge declined.").queue();
            } else if (args[0].equals("!sp")) {
                players.put(event.getAuthor().getId(), new SelfPlayer(event.getAuthor().getId()));
            }
        } else if (players.containsKey(event.getAuthor().getId())) {
            var player = players.get(event.getAuthor().getId());
            try {
                player.makeMove(content);
            } catch (IllegalMoveException e) {
                event.getChannel().sendMessage(e.getMessage()).queue();
            }
            var endState = player.isGameOver();
            if (endState != ChessGame.EndState.NotOver) {
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

                player.getOpponent().resetGameInfo();
                player.resetGameInfo();
            }
        }
    }
}
