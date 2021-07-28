package cantseechess;

import cantseechess.chess.ChessGame;
import cantseechess.chess.Color;
import cantseechess.chess.Rating;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

public interface OngoingGame {
    void playerMove(User sender, Message message);

    void resign(long player);
    void draw(long player);
    void cancelGame();
    void endGame(ChessGame.EndState endState);

    GuildChannel getChannel();

    long getWhiteId();
    long getBlackId();

    Rating getWhiteRating();
    Rating getBlackRating();

    void sendClockSeconds(Color turn, int seconds);
}
