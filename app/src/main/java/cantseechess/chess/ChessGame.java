package cantseechess.chess;

import java.util.ArrayList;
import java.util.Optional;

public class ChessGame {
    private Piece[][] board_pieces = new Piece[8][8];
    private Color turnColor = Color.white;
    //Number of half moves since last capture/pawn move
    private int halfmoveClock = 0;
    private int moves = 1;
    //The square which player turnColor can en passant to
    private Position enPassantSquare = null;
    //Availability to castle
    private Castling castling = new Castling("KQkq");
    private static final String START_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    class Castling {
        public boolean kingSideWhite;
        public boolean queenSideWhite;
        public boolean kingSideBlack;
        public boolean queenSideBlack;

        Castling(String str) {
            kingSideWhite = str.contains("K");
            queenSideWhite = str.contains("Q");
            kingSideBlack = str.contains("k");
            queenSideBlack = str.contains("q");
        }

        public boolean canCastleKingside(Color c) {
            return c == Color.white ? kingSideWhite : kingSideBlack;
        }

        public boolean canCastleQueenside(Color c) {
            return c == Color.white ? queenSideWhite : queenSideBlack;
        }
    }

    public ChessGame() {
        try {
            importFEN(START_FEN);
        } catch (IncorrectFENException e) {
            System.err.println("START_FEN was somehow wrong");
            e.printStackTrace();
        }
    }

    //Import chess game from fen
    public ChessGame(String FEN) throws IncorrectFENException {
        importFEN(FEN);
    }

    public static Piece[][] FENtoBoard(String FEN) throws IncorrectFENException {
        Piece[][] toReturn = new Piece[8][8];
        try {
            String[] content = FEN.split(" ");
            int rank = 7;
            int file = 0;
            //Set all board pieces to Blank (so they're not null)
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    toReturn[i][j] = new Blank();
                }
            }
            for (int i = 0; i < content[0].length(); ++i) {
                char c = content[0].charAt(i);
                if (c > '0' && c < '9') {
                    file += (c - '0');
                    continue;
                }
                switch (c) {
                    case '/':
                        --rank;
                        file = 0;
                        break;
                    case 'P':
                        toReturn[file][rank] = new Pawn(Color.white);
                        ++file;
                        break;
                    case 'N':
                        toReturn[file][rank] = new Knight(Color.white);
                        ++file;
                        break;
                    case 'B':
                        toReturn[file][rank] = new Bishop(Color.white);
                        ++file;
                        break;
                    case 'R':
                        toReturn[file][rank] = new Rook(Color.white);
                        ++file;
                        break;
                    case 'Q':
                        toReturn[file][rank] = new Queen(Color.white);
                        ++file;
                        break;
                    case 'K':
                        toReturn[file][rank] = new King(Color.white);
                        ++file;
                        break;
                    case 'p':
                        toReturn[file][rank] = new Pawn(Color.black);
                        ++file;
                        break;
                    case 'n':
                        toReturn[file][rank] = new Knight(Color.black);
                        ++file;
                        break;
                    case 'b':
                        toReturn[file][rank] = new Bishop(Color.black);
                        ++file;
                        break;
                    case 'r':
                        toReturn[file][rank] = new Rook(Color.black);
                        ++file;
                        break;
                    case 'q':
                        toReturn[file][rank] = new Queen(Color.black);
                        ++file;
                        break;
                    case 'k':
                        toReturn[file][rank] = new King(Color.black);
                        ++file;
                        break;
                    default:
                        throw new IncorrectFENException(null);
                }
            }
            return toReturn;
        } catch (Exception e) {
            throw new IncorrectFENException(e);
        }

    }

    //Return a board position from FEN
    private void importFEN(String FEN) throws IncorrectFENException {
        try {
            board_pieces = FENtoBoard(FEN);
            String[] content = FEN.split(" ");
            //The color of the player whose turn it is
            turnColor = content[1].equalsIgnoreCase("w") ? Color.white : Color.black;
            castling = new Castling(content[2]);
            enPassantSquare = content[3].charAt(0) == '-' ? null : new Position(content[3]);
            halfmoveClock = Integer.parseInt(content[4]);
            moves = Integer.parseInt(content[5]);
        } catch (Exception e) {
            throw new IncorrectFENException(e);
        }

    }

    // returns whether the move was valid or not
    public boolean tryMove(String move, Color turnColor) {
        try {
            getMove(move, turnColor);
            return true;
        } catch (IllegalMoveException e) {
            return false;
        }
    }

    public Move getMove(String move, Color turnColor) throws IllegalMoveException {
        if (move.length() < 2) {
            throw new IllegalMoveException("Bad move formatting");
        }
        // parse algebraic notation
        if (move.matches("\\A(O-O|0-0)\\z")) {
            if (castling.canCastleKingside(turnColor)) {
                // TODO: actually castle
                throw new IllegalMoveException("NYI :(");
            }
            throw new IllegalMoveException("Castling is not possible!");
        }
        if (move.matches("\\A(O-O-O|0-0-0)\\z")) {
            // TODO: queenside castle
            throw new IllegalMoveException("NYI :(");
        }
        var pieceType = move.charAt(0);
        Optional<Class<?>> pieceClass = Optional.empty();
        Position endpoint;
        if (pieceType >= 'a' && pieceType < 'i') {
            var fromFile = pieceType - 'a';
            // pawn move
            if (move.charAt(1) == 'x') {
                // pawn capture
                endpoint = new Position(move.substring(2));
            } else {
                endpoint = new Position(move);
            }
            for (int rank = 0; rank < 8; ++rank) {
                var piece = getPiece(rank, fromFile);
                if (piece.getColor() == turnColor && piece instanceof Pawn) {
                    if (piece.canMove(board_pieces, new Position(rank, fromFile), endpoint)) {
                        return new Move(new Position(rank, fromFile), endpoint);
                    }
                    throw new IllegalMoveException("Illegal pawn move");
                }
            }
            throw new IllegalMoveException("Illegal pawn move");
        } else {
            switch (pieceType) {
                case 'n':
                case 'N':
                    pieceClass = Optional.of(Knight.class);
                    break;
                case 'K':
                    pieceClass = Optional.of(King.class);
                    break;
                case 'R':
                    pieceClass = Optional.of(Rook.class);
                    break;
                case 'Q':
                    pieceClass = Optional.of(Queen.class);
                    break;
                case 'B':
                    pieceClass = Optional.of(Bishop.class);
                    break;

            }
            var p = move.charAt(1);
            if (p == 'x') {
                // capture
                endpoint = new Position(move.substring(2));
            } else {
                endpoint = new Position(move.substring(1));
            }
        }
        if (pieceClass.isEmpty()) {
            throw new IllegalMoveException("Invalid piece");
        }
        for (int rank = 0; rank < 8; ++rank) {
            for (int file = 0; file < 8; ++file) {
                var piece = board_pieces[file][rank];
                if (piece.getColor() == turnColor && pieceClass.get().isInstance(piece)) {
                    if (board_pieces[file][rank].canMove(board_pieces, new Position(rank, file), endpoint)) {
                        return new Move(new Position(rank, file), endpoint);
                    }
                }
            }
        }

        throw new IllegalMoveException("Illegal move");
    }

    public String getFEN() {
        var builder = new StringBuilder();
        var blanks = 0;
        for (int rank = 7; rank >= 0; --rank) {
            for (int file = 0; file < 8; ++file) {
                var piece = board_pieces[file][rank];
                if (piece.isBlank()) {
                    ++blanks;
                } else {
                    if (blanks > 0) {
                        builder.append(blanks);
                        blanks = 0;
                    }
                    char p = '?';
                    if (piece instanceof Pawn) {
                        p = 'p';
                    } else if (piece instanceof Rook) {
                        p = 'r';
                    } else if (piece instanceof King) {
                        p = 'k';
                    } else if (piece instanceof Queen) {
                        p = 'q';
                    } else if (piece instanceof Knight) {
                        p = 'n';
                    } else if (piece instanceof Bishop) {
                        p = 'b';
                    }
                    builder.append(piece.getColor() == Color.white ? Character.toUpperCase(p) : p);
                }
            }
            if (blanks > 0) {
                builder.append(blanks);
                blanks = 0;
            }
            if (rank > 0) {
                builder.append('/');
            }
        }
        // TODO: turn color, half clock, en passant, etc needs to be in the FEN.
        return builder.toString();
    }

    public void makeMove(Move m) {
        board_pieces[m.to.getFile()][m.to.getRank()] = getPiece(m.from);
        board_pieces[m.from.getFile()][m.from.getRank()] = new Blank();
    }

    public enum EndState {
        NotOver,
        WhiteWins,
        BlackWins,
        Draw,
    }

    public EndState isGameOver() {
        return isInStalemate() ? EndState.Draw : isInCheckmate();
    }

    public boolean isInStalemate() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board_pieces[i][j];
                if (piece.getColor() != turnColor) continue;
                for (int ii = 0; ii < 8; ii++) {
                    for (int jj = 0; jj < 8; jj++) {
                        Piece testPos = board_pieces[ii][jj];
                        if (piece.canMove(board_pieces, new Position(j, i), new Position(jj, ii))) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    //Checks if either side is in checkmate and returns the respective endstate
    public EndState isInCheckmate() {
        Piece king = findKing(turnColor);
        Position kingPos = findPosition(king);
        var piecesAttackingKing = attackingPieces(king, kingPos);

        EndState mateState = turnColor == Color.white ? EndState.BlackWins : EndState.WhiteWins;

        if (piecesAttackingKing.size() == 0) return EndState.NotOver;

        if (piecesAttackingKing.size() == 2) {
            for (int file = 0; file < 8; file++) {
                for (int rank = 0; rank < 8; rank++) {
                    Piece p = board_pieces[file][rank];
                    if (king.canMove(board_pieces, kingPos, new Position(rank, file)) && attackingPieces(p, new Position(rank, file)).size() == 0)
                        return EndState.NotOver;

                }
            }
            return mateState;
        }

        Piece attackPiece = piecesAttackingKing.get(0);
        Position attackPiecePos = findPosition(attackPiece);
        for (int file = 0; file < 8; file++) {
            for (int rank = 0; rank < 8; rank++) {
                Piece p = board_pieces[file][rank];
                //if the piece can capture the piece attacking the king
                if (p.canMove(board_pieces, new Position(rank, file), attackPiecePos)) {
                    return EndState.NotOver;
                }
            }
        }

        return mateState;
    }

    public Position findPosition(Piece piece) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board_pieces[i][j] == piece) {
                    return new Position(j, i);
                }
            }
        }
        return null;
    }

    //Finds king of color color
    public Piece findKing(Color color) {
        for (Piece[] p : board_pieces) {
            for (Piece piece : p) {
                if (piece instanceof King && piece.getColor() == color)
                    return piece;
            }
        }
        return null;
    }

    public ArrayList<Piece> attackingPieces(Piece piece, Position position) {
        ArrayList<Piece> attackingPieces = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece pie = board_pieces[i][j];
                if (!pie.matchesColor(piece) && !pie.isBlank() && pie.canMove(board_pieces, new Position(j, i), position)) {
                    attackingPieces.add(pie);
                }
            }
        }
        return attackingPieces;
    }


    public Piece getPiece(Position position) {
        return board_pieces[position.getFile()][position.getRank()];
    }

    // will not throw an IndexOutOfBoundsException
    private Piece getPieceSafe(int file, int rank) {
        if (file < 0 || file > 7 || rank < 0 || rank > 7) {
            return null;
        }
        return board_pieces[file][rank];
    }

    private Piece getPiece(int rank, int file) {
        return board_pieces[file][rank];
    }

    public Piece[][] getPieces() {
        return board_pieces;
    }

    public static class Move {
        final Position from;
        final Position to;

        Move(Position from, Position to) {
            this.to = to;
            this.from = from;
        }
    }
}
