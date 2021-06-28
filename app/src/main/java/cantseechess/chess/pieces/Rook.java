package cantseechess.chess.pieces;

import cantseechess.chess.Color;
import cantseechess.chess.Position;

public class Rook extends Piece {
    public Rook(Color color) {
        super(color);
    }

    @Override
    void canMove(Piece[][] board, Position from, Position to) {
        /*
        boolean movingCorrect = (currPos.x == newPos.x && !(newPos.y == currPos.y)) || (currPos.y == newPos.y && !(newPos.x == currPos.x));

        //You're correctly moving
        if (movingCorrect && searchForPiece(chessBoard, newPos, currPos, currPos)) {
            return NORMAL;
        }
        //You're moving incorrectly
        return NONE;
         */
    }

}
