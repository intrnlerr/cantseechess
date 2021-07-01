package cantseechess.chess;

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
        // parse algebraic notation
        if (move.matches("\\A(O-O|0-0)\\z")) {
            if (castling.canCastleKingside(turnColor)) {
                // TODO: actually castle
                return true;
            }
            return false;
        }
        if (move.matches("\\A(O-O-O|0-0-0)\\z")) {
            // TODO: queenside castle
            return true;
        }
        var pieceType = move.charAt(0);
        if (pieceType >= 'a' && pieceType < 'i') {
            // pawn move
            var endPos = new Position(move);
            for (int r = 0; r < 7; ++r) {
                var p = getPiece(r, endPos.getFile());
                if (p instanceof Pawn && p.getColor() == turnColor) {
                    //
                    return true;
                }
            }
            return false;
        } else {

        }
        return false;
    }

    public Piece getPiece(Position position) {
        return board_pieces[position.getFile()][position.getRank()];
    }

    private Piece getPiece(int rank, int file) {
        return board_pieces[file][rank];
    }
}
