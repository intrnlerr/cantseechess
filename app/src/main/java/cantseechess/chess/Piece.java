package cantseechess.chess;

public abstract class Piece {
    private final Color color;

    abstract boolean canMove(Piece[][] board, Position from, Position to);

    public boolean matchesColor(Piece other) {
        if (other.isBlank()) return false;
        return other.getColor() == this.getColor();
    }

    @Untested
    public boolean isBlank() {
        return this instanceof Blank;
    }

    public Piece(Color color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    //you should only have to use matchesColor() everywhere but here
    protected Color getColor() {
        return color;
    }

    @Untested
    protected boolean searchDiagonal(Piece[][] board, Position from, Position to, Position sp) {
        Piece search = board[sp.getFile()][sp.getRank()];

        int horizontalChange = to.getRank() - from.getRank();
        int verticalChange = to.getFile() - from.getFile();

        //Otherwise it would directly change the original search position
        Position searchPos = new Position(sp.getRank(), sp.getFile());

        //If the piece that the search function is currently selecting is not a blank piece then return false, as something is blocking the bishop's way
        if ((!search.isBlank() && !searchPos.equals(to)) && !searchPos.equals(from)) {
            return false;
        }
        //If the tile has been reached, and the bishop can move there
        else if (!search.matchesColor(board[from.getFile()][from.getRank()]) && (searchPos.equals(to))) {
            return true;
        }
        //If the tile has been reached, and the bishop can't move there
        else if ((searchPos.equals(to)) && search.matchesColor(board[from.getFile()][from.getRank()])) {
            return false;
        }
        //The search function will recursively run until it reaches the specified position.
        else {
            //Setting search position one up-right
            if (horizontalChange == -verticalChange && verticalChange < 0) {
                searchPos = new Position(searchPos.getRank() + 1, searchPos.getFile() - 1);
            }
            //Setting search position one up-left
            else if (horizontalChange == verticalChange && verticalChange < 0) {
                searchPos = new Position(searchPos.getRank() - 1, searchPos.getFile() - 1);
            }
            //Setting search position one down-right
            else if (horizontalChange == verticalChange && verticalChange > 0) {
                searchPos = new Position(searchPos.getRank() + 1, searchPos.getFile() + 1);
            }
            //Setting search position one down-left
            else {
                searchPos = new Position(searchPos.getRank() - 1, searchPos.getFile() + 1);
            }
            return searchDiagonal(board, from, to, searchPos);
        }
    }

    @Untested
    protected boolean searchStraight(Piece[][] board, Position from, Position to, Position sp) {
        
        if (to.equals(from)) return false;
        Piece search = board[sp.getFile()][sp.getRank()];

        //Otherwise it would directly change the original search position which was causing bugs
        Position searchPos = new Position(sp.getRank(), sp.getFile());

        //If the piece that the search function is currently selecting is not a blank piece then return false, as something is blocking the rook's way
        if ((!search.isBlank() && !searchPos.equals(to)) && !searchPos.equals(from)) {
            return false;
        }
        //If the tile has been reached, and the rook can move there
        else if (search.matchesColor(board[from.getFile()][from.getRank()]) && searchPos.equals(to)){
            return true;
        }
        else if (search.matchesColor(board[from.getFile()][from.getRank()]) && searchPos.equals(to)) {
            return false;
        }
        //The search function will recursively run until it reaches the specified position.
        else {
            //Setting search position one downwards
            if (searchPos.getRank() == from.getRank() && searchPos.getFile() < to.getFile()) {
                searchPos = new Position(searchPos.getRank(), searchPos.getFile() + 1);
            }
            //Setting search position one upwards
            else if (searchPos.getRank() == from.getRank() && searchPos.getFile() > to.getFile()) {
                searchPos = new Position(searchPos.getRank(), searchPos.getFile() - 1);
            }
            //Setting search position one to the right
            else if (searchPos.getFile() == from.getFile() && searchPos.getRank() < to.getRank()) {
                searchPos = new Position(searchPos.getRank() + 1, searchPos.getFile());
            }
            //Setting search position one to the left
            else if (searchPos.getFile() == from.getFile() && searchPos.getRank() > to.getRank()){
                searchPos = new Position(searchPos.getRank() - 1, searchPos.getFile());
            }
            //Continue searching.
            return searchStraight(board, from, to, searchPos);
        }
    }
}

class King extends Piece {
    public King(Color color) {
        super(color);
    }

    @Untested
    @Override
    boolean canMove(Piece[][] board, Position from, Position to) {
        
        int verticalChange = Math.abs(to.getFile() - from.getFile());
        int horizontalChange = Math.abs(to.getRank() - from.getRank());
        if (!board[to.getFile()][to.getRank()].matchesColor(board[from.getFile()][from.getRank()]) && (verticalChange + horizontalChange == 1 || (verticalChange == 1 && horizontalChange == 1))) {
            return true;
        }
        return false;
         
    }
}

class Queen extends Piece {
    public Queen(Color color) {
        super(color);
    }

    @Untested
    @Override
    boolean canMove(Piece[][] board, Position from, Position to) {
        int horizontalChange = Math.abs(to.getRank() - from.getRank());
        int verticalChange = Math.abs(to.getFile() - from.getFile());
        if (!board[to.getFile()][to.getRank()].matchesColor(board[from.getFile()][from.getRank()])) {
            if ((horizontalChange == verticalChange && searchDiagonal(board, from, to, from))) return true;
            else if ((from.getRank() == to.getRank() && !(to.getFile() == from.getFile())) || (from.getFile() == to.getFile() && !(to.getRank() == from.getRank()) && searchStraight(board, from, to, from))) return true;
            return false;
        }
        else return false;
    }
}

class Rook extends Piece {
    public Rook(Color color) {
        super(color);
    }

    @Untested
    @Override
    boolean canMove(Piece[][] board, Position from, Position to) {
        boolean movingCorrect = (from.getRank() == to.getRank() && !(to.getFile() == from.getFile())) || (from.getFile() == to.getFile() && !(to.getRank() == from.getRank()));

        //You're correctly moving
        if (movingCorrect && searchStraight(board, from, to, from)) {
            return true;
        }
        //You're moving incorrectly
        return false;

    }

}

class Bishop extends Piece {
    public Bishop(Color color) {
        super(color);
    }

    @Untested
    @Override
    boolean canMove(Piece[][] board, Position from, Position to) {

        int horizontalChange = Math.abs(to.getRank() - from.getRank());
        int verticalChange = Math.abs(to.getFile() - from.getFile());

        if (horizontalChange == verticalChange && searchDiagonal(board, from, to, from)) {
            return true;
        }
        else return false;

    }
}

class Knight extends Piece {
    public Knight(Color color) {
        super(color);
    }

    @Untested
    @Override
    boolean canMove(Piece[][] board, Position from, Position to) {

        int horizontalChange = Math.abs(to.getRank() - from.getRank());
        int verticalChange = (Math.abs(to.getFile() - from.getFile()));

        boolean movingCorrect = (verticalChange == 2 && horizontalChange == 1) || (verticalChange == 1 && horizontalChange == 2);

        if (!board[to.getFile()][to.getRank()].matchesColor(board[from.getFile()][from.getRank()]) && movingCorrect) {
            return true;
        }
        else return false;

    }
}

class Pawn extends Piece {
    boolean hasMoved = false;

    public Pawn(Color color) {
        super(color);
    }

    @Untested
    @Override
    boolean canMove(Piece[][] board, Position from, Position to) {


        int verticalChange = to.getFile() - from.getFile();
        int horizontalChange = to.getRank() - from.getRank();
        int verticalLimit = 1;

        //If you haven't moved yet then you are able to move 2 spaces instead of 1.
        if (!hasMoved) {
            verticalLimit = 2;
        }

        Piece moveToPiece = board[to.getFile()][to.getRank()];
        //If the pawn is trying to move backwards return false
        if ((board[from.getFile()][from.getRank()].getColor() == Color.black && verticalChange < 0) || (board[from.getFile()][from.getRank()].getColor() == Color.white && verticalChange > 0 ))
            return false;

        //If the path your location is completely blank
        boolean canMoveToPos = (moveToPiece.isBlank() && Math.abs(verticalChange) == 1)  || (Math.abs(verticalChange) == 2 && board[from.getFile() - 2][from.getRank()].isBlank() && board[from.getFile() - 1][from.getRank()].isBlank());

        //If the piece ahead is blank and you clicked to a piece you can actually move to or you are attacking a piece then return true (
        if ((canMoveToPos && Math.abs(verticalChange) <= verticalLimit && horizontalChange == 0)
                || (Math.abs(verticalChange) == 1 && Math.abs(horizontalChange) == 1 && moveToPiece.matchesColor(board[from.getFile()][from.getRank()]))){
            return true;
        }
        else {
            return false;
        }

    }
}

class Blank extends Piece {
    public Blank() {
        super(null);
    }
    @Override
    boolean canMove(Piece[][] board, Position from, Position to) {
        return false;
    }
}