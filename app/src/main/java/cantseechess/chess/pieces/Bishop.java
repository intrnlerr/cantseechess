package cantseechess.chess.pieces;

import cantseechess.chess.Color;
import cantseechess.chess.Position;

public class Bishop extends Piece {
    public Bishop(Color color) {
        super(color);
    }

    @Override
    void canMove(Piece[][] board, Position from, Position to) {
        /*
        int horizontalChange = Math.abs(newPos.x - currPos.x);
        int verticalChange = Math.abs(newPos.y - currPos.y);

        if (horizontalChange == verticalChange && searchForPiece(chessBoard, newPos, currPos, currPos)) {
            return NORMAL;
        }
        else return NONE;
         */
    }
}
