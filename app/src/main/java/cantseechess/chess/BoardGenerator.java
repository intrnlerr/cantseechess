package cantseechess.chess;

import net.dv8tion.jda.api.entities.Emote;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;


public class BoardGenerator {
    private Emote[] boardEmotes;
    private final OpeningReader openings;

    public BoardGenerator() {
        openings = new OpeningReader();
    }


    public BoardState getBoard(ChessGame game, String FEN) {
        Piece[][] pieces = game.getPieces();
        Emote[][] state = new Emote[8][8];
        for (int file = 0; file < 8; file++) {
            for (int rank = 0; rank < 8; rank++) {
                state[7 - file][rank] = getEmote(pieces[rank][file], file, rank);
            }
        }
        return new BoardState(FEN, state);
    }

    public static ArrayList<ChessGame.Move> getMoves(String PGN) throws IllegalMoveException {
        ArrayList<ChessGame.Move> movesList = new ArrayList<>();
        var game = new ChessGame();

        String[] moves = PGN.replaceAll("[0-9]+\\. *", "").replaceAll("\n", " ").split(" ");

        for (int i = 0; i < moves.length; i++) {
            Color color = i % 2 == 0 ? Color.white : Color.black;

            var move = game.getMove(moves[i], color);
            game.makeMove(move);
            movesList.add(move);
        }
        return movesList;
    }

    public BoardState[] getBoard(List<ChessGame.Move> moves, Consumer<String> opening, @Nullable String startFEN) throws IncorrectFENException {
        ChessGame game;
        if (startFEN != null)
            game = new ChessGame(startFEN);
        else game = new ChessGame();

        BoardState[] states = new BoardState[moves.size() + 1];
        states[0] = getBoard(game, startFEN == null ? game.getFEN() : startFEN);

        for (int i = 0; i < moves.size(); i++) {
            game.makeMove(moves.get(i));
            String FEN = game.getFEN();
            states[i + 1] = getBoard(game, FEN);
            Optional<String> openingString = openings.getOpening(FEN);
            openingString.ifPresent(opening);
        }
        return states;
    }

    private Emote getEmote(Piece p, int file, int rank) {
        StringBuilder toReturn = new StringBuilder();
        String pieceType = "";
        String pieceColor = "";
        String boardColor = "l";
        if (!(p instanceof Blank)) {

            pieceType += p instanceof Knight ? "n" : Character.toLowerCase(p.toString().charAt(0));

            pieceColor += p.getColor() == Color.white ? "l" : "d";

        }

        if ((file & 1) != (rank & 1)) {
            boardColor = "d";
        }

        toReturn.append(pieceType)
                .append(pieceColor)
                .append(boardColor);

        if (toReturn.toString().length() == 1) {
            toReturn.append("_");
        }

        for (Emote e : boardEmotes) {
            if (e.getName().equals(toReturn.toString())) {
                return e;
            }
        }
        return null;
    }

    public void setBoardEmotes(Emote[] boardEmotes) {
        this.boardEmotes = boardEmotes;
    }

    public boolean noEmotes() {
        return this.boardEmotes == null;
    }

    //private static BufferedImage wPawn, wKnight, wRook, wBishop, wQueen, wKing, bPawn, bKnight, bRook, bBishop, bQueen, bKing;
    //private static boolean piecesInit = false;
    //private static final String PIECE_ARRAY_URL = "/ChessPiecesArray.png";
    //private static final String BOARD_URL = "/Chessboard.png";
    //private static BufferedImage BOARD_IMAGE;
    //private static final int PIECE_WIDTH = 128;
    //private static final int PIECE_HEIGHT = 128;

    /*
    public BoardState[] getBoard(String PGN, Optional<String> startFEN) throws IncorrectFENException, IllegalMoveException {
        ChessGame game;
        if (startFEN.isPresent())
             game = new ChessGame(startFEN.get());
        else game = new ChessGame();

        PGN = PGN.replaceAll("([0-9]+[.])", "");
        if (PGN.charAt(0) == ' ') PGN = PGN.substring(1);
        String[] moves = PGN.split(" ");

        BoardState[] states = new BoardState[moves.length+1];
        states[0] = new BoardState(startFEN.orElseGet(game::getFEN), BoardGenerator.getBoard(game.getPieces()));
        try {
            ImageIO.write(states[0].image, "png", new File("crabdance.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < moves.length; i++) {
            Color color = i%2 == 0 ? Color.white : Color.black;
            game.makeMove(game.getMove(moves[i], color));
            String FEN = game.getFEN();
            states[i+1] = new BoardState(FEN, getBoard(game.getPieces()));
            try {
                ImageIO.write(states[i+1].image, "png", new File("test" + i + ".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return states;
    }

    public static final BufferedImage getBoard(String FEN) throws IncorrectFENException {
        return getBoard(ChessGame.FENtoBoard(FEN));
    }

    public static final BufferedImage getBoard(Piece[][] board) {
        if (!piecesInit) initializePieces();

        BufferedImage toReturn = new BufferedImage(BOARD_IMAGE.getColorModel(),  BOARD_IMAGE.copyData(null), BOARD_IMAGE.isAlphaPremultiplied(), null);
        Graphics2D gr = toReturn.createGraphics();
        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                Piece p = board[i][j];
                if (p instanceof Blank) continue;
                Image img = getImage(p);
                gr.drawImage(img, BOARD_IMAGE.getWidth() / 8 * i, BOARD_IMAGE.getHeight() / 8 * (7 - j), null);
            }
        }
        gr.dispose();
        return toReturn;
    }

    private static BufferedImage getImage(Piece p) {
        BufferedImage toReturn = null;
        if (p instanceof Blank) return null;

        if (p instanceof Pawn) {
            toReturn = p.getColor() == Color.black ? bPawn : wPawn;
        } else if (p instanceof Knight) {
            toReturn = p.getColor() == Color.black ? bKnight : wKnight;
        } else if (p instanceof Bishop) {
            toReturn = p.getColor() == Color.black ? bBishop : wBishop;
        } else if (p instanceof Rook) {
            toReturn = p.getColor() == Color.black ? bRook : wRook;
        } else if (p instanceof King) {
            toReturn = p.getColor() == Color.black ? bKing : wKing;
        } else if (p instanceof Queen) {
            toReturn = p.getColor() == Color.black ? bQueen : wQueen;
        }
        return toReturn;
    }

    private static void initializePieces() {
        BufferedImage pieceArray;
        try {
            pieceArray = ImageIO.read(BoardGenerator.class.getResourceAsStream(PIECE_ARRAY_URL));
            BOARD_IMAGE = ImageIO.read(BoardGenerator.class.getResourceAsStream(BOARD_URL));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        BufferedImage[] pieces = new BufferedImage[12];
        for (int i = 0; i < pieces.length; i++) {
            pieces[i] = new BufferedImage(PIECE_WIDTH, PIECE_HEIGHT, pieceArray.getType());

            Graphics2D gr = pieces[i].createGraphics();
            int widthMultiplier = i % 6;
            int heightMultiplier = i / 6;
            gr.drawImage(pieceArray, 0, 0, PIECE_WIDTH, PIECE_HEIGHT, PIECE_WIDTH * widthMultiplier, PIECE_HEIGHT * heightMultiplier, PIECE_WIDTH * (widthMultiplier + 1), PIECE_HEIGHT * (heightMultiplier + 1), null);
            gr.dispose();
        }

        wKing = pieces[0];
        wQueen = pieces[1];
        wBishop = pieces[2];
        wKnight = pieces[3];
        wRook = pieces[4];
        wPawn = pieces[5];
        bKing = pieces[6];
        bQueen = pieces[7];
        bBishop = pieces[8];
        bKnight = pieces[9];
        bRook = pieces[10];
        bPawn = pieces[11];
        piecesInit = true;
    }*/
}