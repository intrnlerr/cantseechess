package cantseechess.chess.pieces;

import cantseechess.chess.Color;
import cantseechess.chess.Position;

public class King extends Piece {
    public King(Color color) {
        super(color);
    }

    @Override
    void canMove(Piece[][] board, Position from, Position to) {
        /*
        int verticalChange = Math.abs(newPos.y - currPos.y);
        int horizontalChange = Math.abs(newPos.x - currPos.x);
        int horizontalChangeSign = 0;
        if (horizontalChange != 0)
            horizontalChangeSign = (newPos.x - currPos.x)/horizontalChange;
        ChessTile rook = chessBoard[newPos.y][newPos.x];
        ChessTile king = chessBoard[currPos.y][currPos.x];
        if (rook.getPiece().isAlly() && rook.getPiece().hasMoved == false && rook.getPiece() instanceof Rook && this.hasMoved == false) {
            if (ChessGame.isUnderAttack(king).length == 0
                    && ChessGame.isUnderAttack(chessBoard[currPos.y][currPos.x + horizontalChangeSign]).length == 0
                    && chessBoard[currPos.y][currPos.x + horizontalChangeSign].getPiece() instanceof Blank
                    && ChessGame.isUnderAttack(chessBoard[currPos.y][currPos.x + horizontalChangeSign*2]).length == 0
                    && chessBoard[currPos.y][currPos.x + horizontalChangeSign*2].getPiece() instanceof Blank)
                if ((horizontalChange == 4 && chessBoard[currPos.y][currPos.x + horizontalChangeSign*3].getPiece() instanceof Blank) || horizontalChange == 3)
                    return CASTLE;
        }
        else if (!chessBoard[newPos.y][newPos.x].getPiece().isAlly() && (verticalChange + horizontalChange == 1 || (verticalChange == 1 && horizontalChange == 1))) {
            return NORMAL;
        }
        return NONE;
         */
    }
}
