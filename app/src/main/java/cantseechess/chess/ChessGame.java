package cantseechess.chess;

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

    //Return a board position from FEN
    private void importFEN(String FEN) throws IncorrectFENException {
        try {
            String[] content = FEN.split(" ");
            int rank = 7;
            int file = 0;
            //Set all board pieces to Blank (so they're not null)
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    board_pieces[i][j] = new Blank();
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
                        board_pieces[file][rank] = new Pawn(Color.white);
                        ++file;
                        break;
                    case 'N':
                        board_pieces[file][rank] = new Knight(Color.white);
                        ++file;
                        break;
                    case 'B':
                        board_pieces[file][rank] = new Bishop(Color.white);
                        ++file;
                        break;
                    case 'R':
                        board_pieces[file][rank] = new Rook(Color.white);
                        ++file;
                        break;
                    case 'Q':
                        board_pieces[file][rank] = new Queen(Color.white);
                        ++file;
                        break;
                    case 'K':
                        board_pieces[file][rank] = new King(Color.white);
                        ++file;
                        break;
                    case 'p':
                        board_pieces[file][rank] = new Pawn(Color.black);
                        ++file;
                        break;
                    case 'n':
                        board_pieces[file][rank] = new Knight(Color.black);
                        ++file;
                        break;
                    case 'b':
                        board_pieces[file][rank] = new Bishop(Color.black);
                        ++file;
                        break;
                    case 'r':
                        board_pieces[file][rank] = new Rook(Color.black);
                        ++file;
                        break;
                    case 'q':
                        board_pieces[file][rank] = new Queen(Color.black);
                        ++file;
                        break;
                    case 'k':
                        board_pieces[file][rank] = new King(Color.black);
                        ++file;
                        break;
                    default:
                        throw new IncorrectFENException(null);
                }
            }
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
        // TODO: check for game over!
        return EndState.NotOver;
    }

    public Piece getPiece(Position position) {
        return board_pieces[position.getFile()][position.getRank()];
    }

    private Piece getPiece(int rank, int file) {
        return board_pieces[file][rank];
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
