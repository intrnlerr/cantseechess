package cantseechess;

import cantseechess.chess.ChessGame;
import cantseechess.chess.Rating;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

public interface OngoingGame {
    ChessGame.EndState playerMove(User sender, Message message);

    void resign(long player);
    void endGame(ChessGame.EndState endState);

    GuildChannel getChannel();

    long getWhiteId();
    long getBlackId();

    Rating getWhiteRating();
    Rating getBlackRating();
}
