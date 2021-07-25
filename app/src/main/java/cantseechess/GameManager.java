package cantseechess;

import cantseechess.chess.ChessGame;
import cantseechess.chess.IncorrectFENException;
import cantseechess.chess.Rating;
import cantseechess.storage.RatingStorage;
import net.dv8tion.jda.api.entities.*;

import java.util.HashMap;
import java.util.Timer;

public class GameManager {
    private final RatingStorage ratings;
    private final BoardMessageManager boardMessageManager;
    private final HashMap<Long, OngoingGame> games = new HashMap<>();
    private final HashMap<Guild, GuildBoardData> availableChannels = new HashMap<>();
    private final Timer chessClocks = new Timer();

    public GameManager(RatingStorage ratings, BoardMessageManager boardMessageManager) {
        this.ratings = ratings;
        this.boardMessageManager = boardMessageManager;
    }

    public void addGuild(Guild g) {
        availableChannels.put(g, new GuildBoardData());
    }

    public void addChannel(Guild guild, long channelId) {
        availableChannels.computeIfAbsent(guild, k -> new GuildBoardData()).addChannel(channelId);
    }

    public void startGame(Guild guild, Challenge challenge) {
        var available = availableChannels.get(guild);
        var channelId = available.pollChannel();
        if (channelId == null) {
            available.queueChallenge(challenge);
            return;
        }
        var channel = guild.getTextChannelById(channelId);
        if (channel == null) {
            throw new NullPointerException("channelId is not a real channel??");
        }
        var challenged = Long.parseLong(challenge.challenged);
        var challenger = Long.parseLong(challenge.challenger);
        ChessGame chessGame;
        if (challenge.startFen != null) {
            try {
                chessGame = new ChessGame(challenge.startFen);
            } catch (IncorrectFENException e) {
                e.printStackTrace();
                return;
            }
        } else {
            chessGame = new ChessGame();
        }
        var game = new TextChannelOngoing(boardMessageManager,
                this,
                channel,
                chessGame,
                challenged,
                challenger,
                ratings.getRating(challenged),
                ratings.getRating(challenger)
        );
        chessClocks.scheduleAtFixedRate(new ChessClockTask(
                game,
                challenge.time,
                challenge.time,
                challenge.increment
        ), 1000, 1000);
        games.put(channel.getIdLong(), game);
    }

    public void resignGame(long channelId, User resigner) {
        games.get(channelId).resign(resigner.getIdLong());
    }

    public void handleGameEnd(OngoingGame game, ChessGame.EndState endState, boolean adjustRating) {
        var channel = game.getChannel();
        var guild = channel.getGuild();
        games.remove(channel.getIdLong());
        var guildBoards = availableChannels.get(guild);
        if (guildBoards == null) {
            throw new NullPointerException("game was played in a guild with no data??");
        }
        var newchal = guildBoards.returnChannel(channel.getIdLong());
        newchal.ifPresent(challenge -> startGame(guild, challenge));

        // adjust rating
        if (!adjustRating) {
            return;
        }

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
        availableChannels.get(guild).dropChannel(channel.getIdLong());
    }

    public boolean isAvailableBoard(TextChannel channel) {
        return availableChannels.get(channel.getGuild()).isAvailableBoard(channel.getIdLong());
    }

    public void playMove(MessageChannel channel, Message message) {
        var ongoingGame = games.get(channel.getIdLong());
        if (ongoingGame == null) {
            return;
        }
        ongoingGame.playerMove(message.getAuthor(), message);
    }

    public void sendDrawRequest(AbstractChannel channel, User sender) {
        var ongoingGame = games.get(channel.getIdLong());
        if (ongoingGame == null) {
            return;
        }
        ongoingGame.draw(sender.getIdLong());
    }
}
