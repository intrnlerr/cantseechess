package cantseechess;

public class Challenge {
    public final String challenger;
    public final String challenged;

    public Challenge(String challenger, String challenged) {
        this.challenger = challenger;
        this.challenged = challenged;
    }

    public ChessGame accept() {
        return new ChessGame();
    }
}
