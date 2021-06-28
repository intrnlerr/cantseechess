package cantseechess;

public class IncorrectFENException extends Exception {
    public IncorrectFENException(Throwable err) {
        super("FEN imported incorrectly", err);
    }
}
