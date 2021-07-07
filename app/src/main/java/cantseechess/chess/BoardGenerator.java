package cantseechess.chess;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class BoardGenerator {
    private static BufferedImage wPawn, wKnight, wRook, wBishop, wQueen, wKing, bPawn, bKnight, bRook, bBishop, bQueen, bKing;
    private static boolean piecesInit = false;
    private static final String PIECE_ARRAY_URL = System.getProperty("user.dir") + "\\app\\src\\main\\java\\cantseechess\\resources\\ChessPiecesArray.png";
    private static final String BOARD_URL = System.getProperty("user.dir") + "\\app\\src\\main\\java\\cantseechess\\resources\\Chessboard.png";
    private static BufferedImage BOARD_IMAGE;
    private static final int WIDTH = 60;
    private static final int HEIGHT = 60;

    public static final BufferedImage getBoard(String FEN) throws IOException, IncorrectFENException {
        return getBoard(ChessGame.FENtoBoard(FEN));
    }

    public static final BufferedImage getBoard(Piece[][] board) throws IOException {
        if (!piecesInit) initializePieces();

        BufferedImage toReturn = BOARD_IMAGE;
        Graphics2D gr = toReturn.createGraphics();
        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                Piece p = board[i][j];
                if (p instanceof Blank) continue;
                Image img = getImage(p).getScaledInstance(BOARD_IMAGE.getWidth()/8, BOARD_IMAGE.getHeight()/8, Image.SCALE_DEFAULT);

                gr.drawImage(img, BOARD_IMAGE.getWidth()/8 * i, BOARD_IMAGE.getHeight()/8 * (7-j), null);
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
        }
        else if (p instanceof Knight) {
            toReturn = p.getColor() == Color.black ? bKnight : wKnight;
        }
        else if (p instanceof Bishop) {
            toReturn = p.getColor() == Color.black ? bBishop : wBishop;
        }
        else if (p instanceof Rook) {
            toReturn = p.getColor() == Color.black ? bRook : wRook;
        }
        else if (p instanceof King) {
            toReturn = p.getColor() == Color.black ? bKing : wKing;
        }
        else if (p instanceof Queen) {
            toReturn = p.getColor() == Color.black ? bQueen : wQueen;
        }
        return toReturn;
    }

    private static void initializePieces() throws IOException {
        BufferedImage pieceArray = ImageIO.read(new File(PIECE_ARRAY_URL));
        BOARD_IMAGE = ImageIO.read(new File(BOARD_URL));
        BufferedImage[] pieces = new BufferedImage[12];
        for (int i = 0; i < pieces.length; i++) {

            pieces[i] = new BufferedImage(WIDTH, HEIGHT, pieceArray.getType());

            Graphics2D gr = pieces[i].createGraphics();
            int widthMultiplier = i%6;
            int heightMultiplier = i / 6;
            gr.drawImage(pieceArray, 0, 0, WIDTH, HEIGHT, WIDTH * widthMultiplier, HEIGHT * heightMultiplier, WIDTH *  (widthMultiplier + 1), HEIGHT * (heightMultiplier + 1), null);
            gr.dispose();
        }
        bQueen = pieces[0];
        bKing = pieces[1];
        bRook = pieces[2];
        bKnight = pieces[3];
        bBishop = pieces[4];
        bPawn = pieces[5];
        wQueen = pieces[6];
        wKing = pieces[7];
        wRook = pieces[8];
        wKnight = pieces[9];
        wBishop = pieces[10];
        wPawn = pieces[11];
        piecesInit = true;
    }
}
