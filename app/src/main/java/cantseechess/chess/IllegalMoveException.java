package cantseechess.chess;

public class IllegalMoveException extends Exception {
    public IllegalMoveException(Piece piece, Position from, Position to, Throwable err) {
        super("Cannot move " + piece + " from " + from + " to " + to, err);
    }

    public IllegalMoveException(String reason) {
        super(reason);
    }
}
