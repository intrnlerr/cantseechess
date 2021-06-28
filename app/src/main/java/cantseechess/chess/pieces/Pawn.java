package cantseechess.chess.pieces;

import cantseechess.chess.Color;
import cantseechess.chess.Position;

public class Pawn extends Piece {
    boolean hasMoved = false;

    public Pawn(Color color) {
        super(color);
    }

    @Override
    void canMove(Piece[][] board, Position from, Position to) {
        /*

        int verticalChange = newPos.y - currPos.y;
        int horizontalChange = newPos.x - currPos.x;
        int verticalLimit = 1;

        //If you haven't moved yet then you are able to move 2 spaces instead of 1.
        if (hasMoved == false) {
            verticalLimit = 2;
        }

        ChessPiece moveToPiece = chessBoard[newPos.y][newPos.x].getPiece();

        //If the path your location is completely blank
        boolean canMoveToPos = (moveToPiece instanceof Blank && verticalChange == -1)  || (verticalChange == -2 && chessBoard[currPos.y - 2][currPos.x].getPiece() instanceof Blank && chessBoard[currPos.y - 1][currPos.x].getPiece() instanceof Blank);

        //If the piece ahead is blank and you clicked to a piece you can actually move to or you are attacking a piece then return true (
        if ((canMoveToPos && Math.abs(verticalChange) <= verticalLimit && horizontalChange == 0)
                || (verticalChange == -1 && Math.abs(horizontalChange) == 1 && !(moveToPiece instanceof Blank) && !moveToPiece.isAlly())){
            if (newPos.y == 0) {
                return PROMOTION;
            }
            else return NORMAL;
        }

        else if (Math.abs(horizontalChange) == 1 && verticalChange == -1){
            ChessPiece passantPiece = chessBoard[newPos.y+1][newPos.x].getPiece();
            if (!passantPiece.isAlly() && passantPiece instanceof Pawn && ((Pawn) passantPiece).pawnDoubleJumpTurnNumber == ChessGame.getTurnNumber() - 1) {
                return EN_PASSANT;
            }
            else return NONE;
        }
        else {
            return NONE;
        }
         */
    }
}
