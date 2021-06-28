package cantseechess.chess.pieces;

import cantseechess.chess.Color;
import cantseechess.chess.Position;

public class Knight extends Piece {
    public Knight(Color color) {
        super(color);
    }

    @Override
    void canMove(Piece[][] board, Position from, Position to) {
        /*
        int horizontalChange = Math.abs(newPos.x - currPos.x);
        int verticalChange = (Math.abs(newPos.y - currPos.y));

        boolean movingCorrect = (verticalChange == 2 && horizontalChange == 1) || (verticalChange == 1 && horizontalChange == 2);

        if (!chessBoard[newPos.y][newPos.x].getPiece().isAlly() && movingCorrect) {
            System.out.println("Knight at " + currPos + " can move to " + chessBoard[newPos.y][newPos.x]);
            return NORMAL;
        }
        else return NONE;
         */
    }
}
