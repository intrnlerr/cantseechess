package cantseechess;

public abstract class Piece {
    private Color color;
    abstract void canMove(Piece[][] board, Position from, Position to);
    public Piece(Color color) {
        this.color = color;
    }
    //returns true or false whether or not the move can be made (if there's an allied piece in the way)
    boolean search(Piece[][]board, Position from, Position to, boolean diagonal, boolean straight) {
        boolean straightReturn = false;
        boolean diagonalReturn = false;
        if (diagonal) diagonalReturn = searchDiagonal(board, from, to, null);
        if (straight) straightReturn = searchStraight(board, from, to, null);
        return diagonalReturn == diagonal && straightReturn == straight;
    }
    boolean searchDiagonal(Piece[][]board, Position from, Position to, Position searchPos) {
        /*ChessPiece search = chessBoard[sp.y][sp.x].getPiece();

        int horizontalChange = newPos.x - originalPos.x;
        int verticalChange = newPos.y - originalPos.y;

        //Otherwise it would directly change the original search position which was causing bugs
        Position searchPos = new Position(sp.x, sp.y);

        //If the piece that the search function is currently selecting is not a blank piece then return false, as something is blocking the bishop's way
        if ((!(search instanceof Blank) && !(searchPos.equals(newPos))) && !(searchPos.equals(originalPos))) {
            return false;
        }
        //If the tile has been reached, and the bishop can move there
        else if (!search.isAlly() && (searchPos.equals(newPos))) {
            return true;
        }
        //If the tile has been reached, and the bishop can't move there
        else if ((searchPos.equals(newPos)) && search.isAlly()) {
            return false;
        }
        //The search function will recursively run until it reaches the specified position.
        else {
            //Setting search position one up-right
            if (horizontalChange == -verticalChange && verticalChange < 0) {
                searchPos.x++;
                searchPos.y--;
            }
            //Setting search position one up-left
            else if (horizontalChange == verticalChange && verticalChange < 0) {
                searchPos.x--;
                searchPos.y--;
            }
            //Setting search position one down-right
            else if (horizontalChange == verticalChange && verticalChange > 0) {
                searchPos.x++;
                searchPos.y++;
            }
            //Setting search position one down-left
            else {
                searchPos.x--;
                searchPos.y++;
            }
            return searchForPiece(chessBoard, newPos, searchPos, originalPos);
        }*/
        return false;
    }
    boolean searchStraight(Piece[][]board, Position from, Position to, Position searchPos) {
        /*
        if (newPos.equals(originalPos)) return false;
        ChessPiece search = chessBoard[sp.y][sp.x].getPiece();

        //Otherwise it would directly change the original search position which was causing bugs
        Position searchPos = new Position(sp.x, sp.y);

        //If the piece that the search function is currently selecting is not a blank piece then return false, as something is blocking the rook's way
        if ((!(search instanceof Blank) && !searchPos.equals(newPos)) && !searchPos.equals(originalPos)) {
            return false;
        }
        //If the tile has been reached, and the rook can move there
        else if (!search.isAlly() && searchPos.equals(newPos)){
            return true;
        }
        else if (search.isAlly() && searchPos.equals(newPos)) {
            return false;
        }
        //The search function will recursively run until it reaches the specified position.
        else {
            //Setting search position one downwards
            if (searchPos.x == originalPos.x && searchPos.y < newPos.y) {
                searchPos.y++;
            }
            //Setting search position one upwards
            else if (searchPos.x == originalPos.x && searchPos.y > newPos.y) {
                searchPos.y--;
            }
            //Setting search position one to the right
            else if (searchPos.y == originalPos.y && searchPos.x < newPos.x) {
                searchPos.x++;
            }
            //Setting search position one to the left
            else if (searchPos.y == originalPos.y && searchPos.x > newPos.x){
                searchPos.x--;
            }
            //Continue searching.
            return searchForPiece(chessBoard, newPos, searchPos, originalPos);

        }
         */
        return false;
    }
}

class King extends Piece{
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

class Queen extends Piece{
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

class Rook extends Piece{
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

class Bishop extends Piece{
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

class Knight extends Piece{
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

class Pawn extends Piece {
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