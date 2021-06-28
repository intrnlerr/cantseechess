package cantseechess.chess.pieces;

import cantseechess.chess.Color;
import cantseechess.chess.Position;

public abstract class Piece {
    private Color color;

    abstract void canMove(Piece[][] board, Position from, Position to);

    public Piece(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    //returns true or false whether or not the move can be made (if there's an allied piece in the way)
    boolean search(Piece[][] board, Position from, Position to, boolean diagonal, boolean straight) {
        boolean straightReturn = false;
        boolean diagonalReturn = false;
        if (diagonal) diagonalReturn = searchDiagonal(board, from, to, null);
        if (straight) straightReturn = searchStraight(board, from, to, null);
        return diagonalReturn == diagonal && straightReturn == straight;
    }

    boolean searchDiagonal(Piece[][] board, Position from, Position to, Position searchPos) {
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

    boolean searchStraight(Piece[][] board, Position from, Position to, Position searchPos) {
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

