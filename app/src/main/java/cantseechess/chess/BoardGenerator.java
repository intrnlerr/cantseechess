package cantseechess.chess;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class BoardGenerator {
    private static BufferedImage wPawn, wKnight, wRook, wBishop, wQueen, wKing, bPawn, bKnight, bRook, bBishop, bQueen, bKing;
    private static boolean piecesInit = false;
    private static final String PIECE_ARRAY_URL = "/ChessPiecesArray.png";
    private static final String BOARD_URL = "/Chessboard.png";
    private static BufferedImage BOARD_IMAGE;
    private static final int PIECE_WIDTH = 128;
    private static final int PIECE_HEIGHT = 128;

    @Untested
    public static BufferedImage[] getBoard(String PGN, String StartFEN) throws IncorrectFENException, IllegalMoveException {
        ArrayList<BufferedImage> toReturn = new ArrayList<>();
        ChessGame game = new ChessGame(StartFEN);
        PGN = PGN.replaceAll("([0-9]+[.])", "");
        if (PGN.charAt(0) == ' ') {
            PGN = PGN.substring(1);
        }
        String[] moves = PGN.split(" ");
        toReturn.add(getBoard(game.getPieces()));
        for (int i = 0; i < moves.length; i++) {
            //this is probably right
            Color color = i%2 == 0 ? Color.white : Color.black;
            game.makeMove(game.getMove(moves[i], color));
            toReturn.add(getBoard(game.getPieces()));
        }
        return (BufferedImage[]) toReturn.toArray();
    }

    public static BufferedImage getBoard(String FEN) throws IncorrectFENException {
        return getBoard(ChessGame.FENtoBoard(FEN));
    }

    public static BufferedImage getBoard(Piece[][] board) {
        if (!piecesInit) initializePieces();

        BufferedImage toReturn = BOARD_IMAGE;
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
    }
}