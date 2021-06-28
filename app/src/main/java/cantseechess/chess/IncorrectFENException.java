package cantseechess.chess;

public class IncorrectFENException extends Exception {
    public IncorrectFENException(Throwable err) {
        super("FEN imported incorrectly", err);
    }
}
