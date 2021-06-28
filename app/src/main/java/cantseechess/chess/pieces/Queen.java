package cantseechess.chess.pieces;

import cantseechess.chess.Color;
import cantseechess.chess.Position;

public class Queen extends Piece {
    public Queen(Color color) {
        super(color);
    }

    @Override
    void canMove(Piece[][] board, Position from, Position to) {
        /*
        int horizontalChange = Math.abs(newPos.x - currPos.x);
        int verticalChange = Math.abs(newPos.y - currPos.y);
        if (!chessBoard[newPos.y][newPos.x].getPiece().isAlly()
                && (horizontalChange == verticalChange
                || ((currPos.x == newPos.x && !(newPos.y == currPos.y))
                || (currPos.y == newPos.y && !(newPos.x == currPos.x))))
                && searchForPiece(chessBoard, newPos, currPos, currPos)) {
            return NORMAL;
        }
        else return NONE;
         */
    }
}
