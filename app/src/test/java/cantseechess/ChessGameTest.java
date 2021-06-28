package cantseechess;

import cantseechess.chess.ChessGame;
import cantseechess.chess.Color;
import cantseechess.chess.Position;
import cantseechess.chess.pieces.*;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class ChessGameTest {
    @Test
    public void constructorTest() {
        var b = new ChessGame();
        assertTrue(b.getPiece(new Position(0, 0)) instanceof Rook);
        assertTrue(b.getPiece(new Position(0, 1)) instanceof Knight);
        assertTrue(b.getPiece(new Position(0, 2)) instanceof Bishop);
        assertTrue(b.getPiece(new Position(0, 3)) instanceof Queen);
        assertTrue(b.getPiece(new Position(0, 4)) instanceof King);
        assertTrue(b.getPiece(new Position(0, 5)) instanceof Bishop);
        assertTrue(b.getPiece(new Position(0, 6)) instanceof Knight);
        assertTrue(b.getPiece(new Position(0, 7)) instanceof Rook);

        for (int file = 0; file < 7; ++file) {
            assertEquals(Color.white, b.getPiece(new Position(0, file)).getColor());
        }
        //check pawns
        for (int file = 0; file < 7; ++file) {
            assertTrue(b.getPiece(new Position(1, file)) instanceof Pawn);
            assertEquals(Color.white, b.getPiece(new Position(1, file)).getColor());
        }

        assertTrue(b.getPiece(new Position(7, 0)) instanceof Rook);
        assertTrue(b.getPiece(new Position(7, 1)) instanceof Knight);
        assertTrue(b.getPiece(new Position(7, 2)) instanceof Bishop);
        assertTrue(b.getPiece(new Position(7, 3)) instanceof Queen);
        assertTrue(b.getPiece(new Position(7, 4)) instanceof King);
        assertTrue(b.getPiece(new Position(7, 5)) instanceof Bishop);
        assertTrue(b.getPiece(new Position(7, 6)) instanceof Knight);
        assertTrue(b.getPiece(new Position(7, 7)) instanceof Rook);

        for (int file = 0; file < 7; ++file) {
            assertEquals(Color.black, b.getPiece(new Position(7, file)).getColor());
        }
        //check pawns
        for (int file = 0; file < 7; ++file) {
            assertTrue(b.getPiece(new Position(6, file)) instanceof Pawn);
            assertEquals(Color.black, b.getPiece(new Position(6, file)).getColor());
        }
    }
}
