package cantseechess.chess;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Optional;

public class ChessGame {
    private Piece[][] board_pieces = new Piece[8][8];
    private Color turnColor = Color.white;
    private int moves = 1;
    // counts the number of half-moves that have not been a capture or a pawn move
    private int fiftyMoveRuleCounter = 0;
    //The square which player turnColor can en passant to
    private Position enPassantSquare = null;
    //Availability to castle
    private Castling castling = new Castling("KQkq");
    private static final String START_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    static class Castling {
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

        void clearCastling(Color c) {
            if (c == Color.white) {
                kingSideWhite = false;
                queenSideWhite = false;
            } else {
                kingSideBlack = false;
                queenSideBlack = false;
            }
        }

        public void clearKingside(Color color) {
            if (color == Color.white) {
                kingSideWhite = false;
            } else {
                kingSideBlack = false;
            }
        }

        public void clearQueenside(Color color) {
            if (color == Color.white) {
                queenSideWhite = false;
            } else {
                queenSideBlack = false;
            }
        }

        @Override
        public String toString() {
            String toReturn = (kingSideWhite ? "K" : "") + (queenSideWhite ? "Q" : "") + (kingSideBlack ? "k" : "") + (queenSideBlack ? "q" : "");
            if (toReturn.isEmpty()) toReturn = "-";
            return toReturn;
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
                        System.out.println(content[0]);
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
            fiftyMoveRuleCounter = Integer.parseInt(content[4]);
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

    private Move getCastle(boolean isKingSide, Color color) throws IllegalMoveException {
        Piece king = findKing(color);
        Position kingPos = findPosition(king);
        if (kingPos.getRank() != 0 && kingPos.getRank() != 7) {
            throw new IllegalMoveException("(castling should already fail before this!)");
        }
        if (attackingPieces(king, kingPos).size() > 0) {
            throw new IllegalMoveException("Cannot castle while in check.");
        }
        var direction = isKingSide ? 1 : -1;
        var endFile = isKingSide ? 6 : 2;
        for (int file = kingPos.getFile() + direction; file != endFile; file += direction) {
            if (!getPiece(file, kingPos.getRank()).isBlank()) {
                throw new IllegalMoveException("Cannot castle through a piece.");
            }
            var attackers = attackingPieces(king, new Position(file, kingPos.getRank()));
            if (attackers.size() > 0) {
                throw new IllegalMoveException("Cannot castle through check.");
            }
        }
        if (attackingPieces(king, new Position(endFile, kingPos.getRank())).size() > 0) {
            throw new IllegalMoveException("Cannot castle into check.");
        }
        return new Move(kingPos,
                new Position(endFile, kingPos.getRank()),
                isKingSide ? SpecialMove.KingsideCastle : SpecialMove.QueensideCastle);
    }

    private Position readPosition(Reader reader) throws IOException {
        var n = new char[2];
        if (reader.read(n) != 2 || !isFileChar(n[0]) || !isRankChar(n[1])) {
            throw new IOException("Invalid move");
        }
        return new Position(getFileFromChar(n[0]), getRankFromChar(n[1]));
    }

    private SearchInfo readPieceMove(Reader moveReader) throws IOException {
        Class<?> pieceClass;
        switch (moveReader.read()) {
            case 'n':
            case 'N':
                pieceClass = Knight.class;
                break;
            case 'K':
                pieceClass = King.class;
                break;
            case 'R':
                pieceClass = Rook.class;
                break;
            case 'Q':
                pieceClass = Queen.class;
                break;
            case 'B':
                pieceClass = Bishop.class;
                break;
            default:
                throw new IOException("Invalid piece type");
        }
        int knownRank = -1;
        int knownFile = -1;
        int endFile = -1;
        Position endpoint = null;
        while (true) {
            var r = moveReader.read();
            if (r == -1) {
                break;
            }
            var cr = (char) r;
            if (cr == 'x') {
                endpoint = readPosition(moveReader);
                break;
            } else if (isRankChar(cr)) {
                if (endFile != -1) {
                    endpoint = new Position(endFile, getRankFromChar(cr));
                } else {
                    knownRank = getRankFromChar(cr);
                }
            } else if (isFileChar(cr)) {
                if (knownRank != -1) {
                    // piece has format like R1a4
                    var rank = moveReader.read();
                    if (rank == -1) {
                        throw new IOException("Invalid move formatting");
                    }
                    endpoint = new Position(getFileFromChar(cr), getRankFromChar((char) rank));
                    break;
                }
                if (knownFile == -1) {
                    knownFile = getFileFromChar(cr);
                } else {
                    endFile = getFileFromChar(cr);
                }
            } else {
                break;
            }
        }
        if (endpoint == null && endFile == -1) {
            endpoint = new Position(knownFile, knownRank);
            knownFile = -1;
            knownRank = -1;
        } else if (endpoint == null) {
            throw new IOException("No position given");
        }
        return new SearchInfo(pieceClass, endpoint, knownFile, knownRank);
    }

    public Move getMove(String move, Color turnColor) throws IllegalMoveException {
        if (move.length() < 2) {
            throw new IllegalMoveException("Bad move formatting");
        }
        // parse algebraic notation
        if (move.matches("\\A(O-O|0-0)\\z")) {
            if (castling.canCastleKingside(turnColor)) {
                return getCastle(true, turnColor);
            }
            throw new IllegalMoveException("Castling is not possible!");
        }
        if (move.matches("\\A(O-O-O|0-0-0)\\z")) {
            if (castling.canCastleQueenside(turnColor)) {
                return getCastle(false, turnColor);
            }
            throw new IllegalMoveException("Castling is not possible!");
        }
        var pieceType = move.charAt(0);
        Optional<Class<?>> pieceClass = Optional.empty();
        Position endpoint;
        if (pieceType >= 'a' && pieceType < 'i') {
            var fromFile = pieceType - 'a';
            char promotionType = '?';
            // pawn move
            if (move.charAt(1) == 'x') {
                // pawn capture
                endpoint = new Position(move.substring(2));
                var enPassant = checkEnPassant(endpoint, turnColor);
                if (enPassant != null) {
                    return enPassant;
                }
                if (move.length() > 4 && move.charAt(4) == '=') {
                    promotionType = move.charAt(5);
                }
            } else {
                endpoint = new Position(move);
                if (move.length() > 2 && move.charAt(2) == '=') {
                    promotionType = move.charAt(3);
                }
            }
            for (int rank = 0; rank < 8; ++rank) {
                var piece = getPiece(fromFile, rank);
                if (piece.getColor() == turnColor && piece instanceof Pawn) {
                    if (piece.canMove(board_pieces, new Position(fromFile, rank), endpoint)) {
                        if (endpoint.getRank() == 0 || endpoint.getRank() == 7) {
                            var promotion = SpecialMove.getPromotionFromChar(promotionType);
                            if (promotion == SpecialMove.NotSpecial) {
                                throw new IllegalMoveException("Illegal promotion type");
                            }
                            return new Move(new Position(fromFile, rank), endpoint, promotion);
                        }
                        return new Move(new Position(fromFile, rank), endpoint);
                    }
                    throw new IllegalMoveException("Illegal pawn move");
                }
            }
            throw new IllegalMoveException("Illegal pawn move");
        }
        var moveReader = new StringReader(move);
        SearchInfo info;
        try {
            info = readPieceMove(moveReader);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalMoveException("Move is formatted incorrectly");
        }
        if (info.knownFile != -1 && info.knownRank != -1) {
            var targetPiece = getPiece(info.knownFile, info.knownRank);
            var targetPos = new Position(info.knownFile, info.knownRank);
            if (targetPiece.isBlank() ||
                    targetPiece.getColor() != turnColor ||
                    !info.pieceType.isInstance(targetPiece) ||
                    !targetPiece.canMove(board_pieces, targetPos, info.endpoint)) {
                throw new IllegalMoveException("Illegal move");
            }
            return new Move(targetPos, info.endpoint);
        } else if (info.knownFile != -1) {
            for (int rank = 0; rank < 8; ++rank) {
                var piece = getPiece(info.knownFile, rank);
                if (piece.getColor() == turnColor && info.pieceType.isInstance(piece)) {
                    if (board_pieces[info.knownFile][rank].canMove(board_pieces, new Position(info.knownFile, rank), info.endpoint)) {
                        return new Move(new Position(info.knownFile, rank), info.endpoint);
                    }
                }
            }
            throw new IllegalMoveException("Illegal move");
        } else if (info.knownRank != -1) {
            for (int file = 0; file < 8; ++file) {
                var piece = board_pieces[file][info.knownRank];
                if (piece.getColor() == turnColor && info.pieceType.isInstance(piece)) {
                    if (board_pieces[file][info.knownRank].canMove(board_pieces, new Position(file, info.knownRank), info.endpoint)) {
                        return new Move(new Position(file, info.knownRank), info.endpoint);
                    }
                }
            }
            throw new IllegalMoveException("Illegal move");
        }
        for (int rank = 0; rank < 8; ++rank) {
            for (int file = 0; file < 8; ++file) {
                var piece = board_pieces[file][rank];
                if (piece.getColor() == turnColor && info.pieceType.isInstance(piece)) {
                    if (board_pieces[file][rank].canMove(board_pieces, new Position(file, rank), info.endpoint)) {
                        return new Move(new Position(file, rank), info.endpoint);
                    }
                }
            }
        }

        throw new IllegalMoveException("Illegal move");
    }

    private int getFileFromChar(char p) {
        return p - 'a';
    }

    private boolean isRankChar(char p) {
        return p >= '1' && p <= '8';
    }

    private int getRankFromChar(char p) {
        return p - '1';
    }

    private boolean isFileChar(char p) {
        return p >= 'a' && p <= 'h';
    }

    private Move checkEnPassant(Position endpoint, Color turnColor) {
        if (!endpoint.equals(enPassantSquare)) {
            return null;
        }
        if (turnColor == Color.white) {
            // the pawn that captures en passant can only be on a square
            // which is up and to the left or up and to the right of the endpoint
            var pawn = getPieceSafe(endpoint.getFile() - 1, endpoint.getRank() - 1);
            if (pawn instanceof Pawn && pawn.getColor() == turnColor) {
                return new Move(new Position(endpoint.getRank() - 1, endpoint.getFile() - 1), endpoint, SpecialMove.EnPassant);
            }
            pawn = getPieceSafe(endpoint.getFile() + 1, endpoint.getRank() - 1);
            if (pawn instanceof Pawn && pawn.getColor() == turnColor) {
                return new Move(new Position(endpoint.getFile() + 1, endpoint.getRank() - 1), endpoint, SpecialMove.EnPassant);
            }
        } else {
            var pawn = getPieceSafe(endpoint.getFile() - 1, endpoint.getRank() + 1);
            if (pawn instanceof Pawn && pawn.getColor() == turnColor) {
                return new Move(new Position(endpoint.getRank() - 1, endpoint.getFile() + 1), endpoint, SpecialMove.EnPassant);
            }
            pawn = getPieceSafe(endpoint.getFile() + 1, endpoint.getRank() + 1);
            if (pawn instanceof Pawn && pawn.getColor() == turnColor) {
                return new Move(new Position(endpoint.getFile() + 1, endpoint.getRank() + 1), endpoint, SpecialMove.EnPassant);
            }
        }
        return null;
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
        builder.append(turnColor == Color.white ? " w " : " b ")
                .append(castling.toString() + " ")
                .append(enPassantSquare != null ? enPassantSquare.toString() + " " : "- ")
                .append(fiftyMoveRuleCounter + " ")
                .append(moves);
        return builder.toString();
    }

    public void makeMove(Move m) {
        enPassantSquare = null;
        if (board_pieces[m.from.getFile()][m.from.getRank()] instanceof Pawn) {
            // assume this move is legal
            var rankDiff = m.to.getRank() - m.from.getRank();
            if (rankDiff == 2 || rankDiff == -2) {
                enPassantSquare = new Position(m.to.getFile(), m.to.getRank() - (rankDiff / 2));
            }
        }
        var movedPiece = getPiece(m.to);
        if (!movedPiece.isBlank()) {
            ++fiftyMoveRuleCounter;
        } else {
            // captures reset the count
            fiftyMoveRuleCounter = 0;
        }
        if (movedPiece instanceof Pawn) {
            // pawn moves also reset the count
            fiftyMoveRuleCounter = 0;
        } else if (movedPiece instanceof King) {
            castling.clearCastling(movedPiece.getColor());
        } else if (movedPiece instanceof Rook) {
            if (m.from.getFile() == 7) {
                castling.clearKingside(movedPiece.getColor());
            } else if (m.from.getFile() == 0) {
                castling.clearQueenside(movedPiece.getColor());
            }
        }
        board_pieces[m.to.getFile()][m.to.getRank()] = getPiece(m.from);
        board_pieces[m.from.getFile()][m.from.getRank()] = new Blank();
        if (m.specialMove != SpecialMove.NotSpecial) {
            switch (m.specialMove) {
                case Queen:
                    board_pieces[m.to.getFile()][m.to.getRank()] = new Queen(getPiece(m.to).getColor());
                    break;
                case Rook:
                    board_pieces[m.to.getFile()][m.to.getRank()] = new Rook(getPiece(m.to).getColor());
                    break;
                case Bishop:
                    board_pieces[m.to.getFile()][m.to.getRank()] = new Bishop(getPiece(m.to).getColor());
                    break;
                case Knight:
                    board_pieces[m.to.getFile()][m.to.getRank()] = new Knight(getPiece(m.to).getColor());
                    break;
                case KingsideCastle:
                    // move the rook
                    board_pieces[m.to.getFile() - 1][m.to.getRank()] = board_pieces[7][m.to.getRank()];
                    board_pieces[7][m.to.getRank()] = new Blank();
                    castling.clearCastling(m.to.getRank() == 0 ? Color.white : Color.black);
                    break;
                case QueensideCastle:
                    board_pieces[m.to.getFile() + 1][m.to.getRank()] = board_pieces[0][m.to.getRank()];
                    board_pieces[0][m.to.getRank()] = new Blank();
                    castling.clearCastling(m.to.getRank() == 0 ? Color.white : Color.black);
                    break;
                case EnPassant:
                    var offset = movedPiece.getColor() == Color.white ? -1 : 1;
                    board_pieces[m.to.getFile()][m.to.getRank() + offset] = new Blank();
                    break;
            }

        }
        turnColor = turnColor == Color.white ? Color.black : Color.white;
        moves++;
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
        if (fiftyMoveRuleCounter >= 100) {
            return true;
        }
        boolean legalMove = false;
        boolean checkmatingPiece = false;
        int knights = 0;
        int bishops = 0;
        Position blackBishop = null;
        Position whiteBishop = null;
        for (int file = 0; file < 8; file++) {
            for (int rank = 0; rank < 8; rank++) {
                Piece piece = board_pieces[file][rank];
                if (piece instanceof Pawn) {
                    checkmatingPiece = true;
                } else if (piece instanceof Queen) {
                    checkmatingPiece = true;
                } else if (piece instanceof Rook) {
                    checkmatingPiece = true;
                } else if (piece instanceof Knight) {
                    ++knights;
                } else if (piece instanceof Bishop) {
                    ++bishops;
                    if (piece.getColor() == Color.white) {
                        whiteBishop = new Position(file, rank);
                    } else {
                        blackBishop = new Position(file, rank);
                    }
                }
                if (piece.getColor() != turnColor) continue;
                if (legalMove) {
                    continue;
                }
                for (int ii = 0; ii < 8; ii++) {
                    for (int jj = 0; jj < 8; jj++) {
                        Piece testPos = board_pieces[ii][jj];
                        if (piece.canMove(board_pieces, new Position(file, rank), new Position(ii, jj))) {
                            legalMove = true;
                            break;
                        }
                    }
                }
            }
        }
        if (!legalMove) {
            return true;
        }
        if (checkmatingPiece) {
            return false;
        }
        if ((knights == 0 && bishops == 0) || (knights == 1 && bishops == 0) || (knights == 0 && bishops == 1)) {
            return true;
        }
        if (knights == 0 && bishops == 2) {
            // calculate if the bishops lie on the same colored square
            return (whiteBishop.getRank() + whiteBishop.getFile()) % 2 ==
                    (blackBishop.getRank() + blackBishop.getFile()) % 2;

        }
        return false;
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
                    if (king.canMove(board_pieces, kingPos, new Position(file, rank)) && attackingPieces(p, new Position(file, rank)).size() == 0)
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
                if (p.canMove(board_pieces, new Position(file, rank), attackPiecePos)) {
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
                    return new Position(i, j);
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
                if (!pie.matchesColor(piece) && !pie.isBlank() && pie.canMove(board_pieces, new Position(i, j), position)) {
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

    private Piece getPiece(int file, int rank) {
        return board_pieces[file][rank];
    }

    public Piece[][] getPieces() {
        return board_pieces;
    }

    public static class Move {
        final Position from;
        final Position to;
        final SpecialMove specialMove;

        Move(Position from, Position to) {
            this.to = to;
            this.from = from;
            this.specialMove = SpecialMove.NotSpecial;
        }

        Move(Position from, Position to, SpecialMove specialMove) {
            this.to = to;
            this.from = from;
            this.specialMove = specialMove;
        }
    }

    private static class SearchInfo {
        final Class<?> pieceType;
        final Position endpoint;
        final int knownFile;
        final int knownRank;

        private SearchInfo(Class<?> pieceType, Position endpoint, int knownFile, int knownRank) {
            this.pieceType = pieceType;
            this.endpoint = endpoint;
            this.knownFile = knownFile;
            this.knownRank = knownRank;
        }
    }
}
