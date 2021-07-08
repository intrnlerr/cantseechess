package cantseechess;

import cantseechess.chess.ChessGame;
import net.dv8tion.jda.api.entities.MessageChannel;

public class Challenge {
    public final String challenger;
    public final String challenged;
    public final MessageChannel channel;

    public Challenge(String challenger, String challenged, MessageChannel channel) {
        this.challenger = challenger;
        this.challenged = challenged;
        this.channel = channel;
    }

    public ChessGame accept() {
        return new ChessGame();
    }
}
