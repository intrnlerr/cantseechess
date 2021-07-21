package cantseechess;

import cantseechess.chess.ChessGame;
import cantseechess.chess.Rating;
import cantseechess.storage.RatingStorage;
import net.dv8tion.jda.api.entities.*;

import java.util.ArrayDeque;
import java.util.HashMap;

public class GameManager {
    private final RatingStorage ratings;
    private final BoardMessageManager boardMessageManager;
    private final HashMap<Long, OngoingGame> games = new HashMap<>();
    private final HashMap<Guild, ArrayDeque<String>> availableChannels = new HashMap<>();

    public GameManager(RatingStorage ratings, BoardMessageManager boardMessageManager) {
        this.ratings = ratings;
        this.boardMessageManager = boardMessageManager;
    }

    public void addGuild(Guild g) {
        availableChannels.put(g, new ArrayDeque<>());
    }

    public void addChannel(Guild guild, String channelId) {
        availableChannels.computeIfAbsent(guild, k -> new ArrayDeque<>()).add(channelId);
    }

    public void startGame(Guild guild, Challenge challenge) {
        var available = availableChannels.get(guild);
        var channelId = available.poll();
        // TODO: we should queue up the game when there are no available channels instead of failing
        if (channelId == null) {
            return;
        }
        var channel = guild.getTextChannelById(channelId);
        if (channel == null) {
            throw new NullPointerException("channelId is not a real channel??");
        }
        var challenged = Long.parseLong(challenge.challenged);
        var challenger = Long.parseLong(challenge.challenger);
        var game = new TextChannelOngoing(boardMessageManager, this,
                channel,
                challenged,
                challenger,
                ratings.getRating(challenged),
                ratings.getRating(challenger)
        );
        games.put(channel.getIdLong(), game);
    }

    public void resignGame(long channelId, User resigner) {
        games.get(channelId).resign(resigner.getIdLong());
    }

    public void handleGameEnd(OngoingGame game, ChessGame.EndState endState) {
        var channel = game.getChannel();
        games.remove(channel.getIdLong());
        availableChannels.get(channel.getGuild()).push(channel.getId());
        // adjust rating

        if (endState == ChessGame.EndState.Draw) {
            ratings.addGame(game.getWhiteId(), new Rating.GameEntry(game.getBlackRating(), 0.5));
            ratings.addGame(game.getBlackId(), new Rating.GameEntry(game.getWhiteRating(), 0.5));
        } else if (endState == ChessGame.EndState.WhiteWins) {
            ratings.addGame(game.getWhiteId(), new Rating.GameEntry(game.getBlackRating(), 1));
            ratings.addGame(game.getBlackId(), new Rating.GameEntry(game.getWhiteRating(), 0));
        } else {
            ratings.addGame(game.getWhiteId(), new Rating.GameEntry(game.getBlackRating(), 0));
            ratings.addGame(game.getBlackId(), new Rating.GameEntry(game.getWhiteRating(), 1));
        }
    }

    public void dropChannel(Guild guild, TextChannel channel) {
        availableChannels.get(guild).remove(channel.getId());
    }

    public boolean isAvailableBoard(TextChannel channel) {
        return availableChannels.get(channel.getGuild()).contains(channel.getId());
    }

    public void playMove(MessageChannel channel, Message message) {
        var ongoingGame = games.get(channel.getIdLong());
        if (ongoingGame == null) {
            return;
        }
        ongoingGame.playerMove(message.getAuthor(), message);
    }
}
