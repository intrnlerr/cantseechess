package cantseechess;

import cantseechess.chess.Color;

public class Challenge {
    public final String challenger;
    public final String challenged;
    public final Color challengerColor;
    public final String startFen;
    public final int time;
    public final int increment;

    public Challenge(String challenger, String challenged, Color challengerColor, String startFen, int time, int increment) {
        this.challenger = challenger;
        this.challenged = challenged;
        this.challengerColor = challengerColor;
        this.startFen = startFen;
        this.time = time;
        this.increment = increment;
    }
}
