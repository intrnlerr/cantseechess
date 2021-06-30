package cantseechess.chess;

public class IncorrectMoveException extends Exception{
    public IncorrectMoveException(Piece piece, Position from, Position to, Throwable err) {
        super("Cannot move " + piece+ " from " + from + " to " + to, err);
    }
}
