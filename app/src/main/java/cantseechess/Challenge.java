package cantseechess;

import cantseechess.chess.ChessGame;
import cantseechess.chess.Color;

public class Challenge {
    public final String challenger;
    public final String challenged;
    public final Color challengerColor;
    public final int time;
    public final int increment;

    public Challenge(String challenger, String challenged, Color challengerColor, int time, int increment) {
        this.challenger = challenger;
        this.challenged = challenged;
        this.challengerColor = challengerColor;
        this.time = time;
        this.increment = increment;
    }

    public ChessGame accept() {
        return new ChessGame();
    }
}
