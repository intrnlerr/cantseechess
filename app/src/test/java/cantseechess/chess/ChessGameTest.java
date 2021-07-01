package cantseechess.chess;

import org.junit.Test;

import static junit.framework.TestCase.*;

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

    @Test
    public void whiteFirstMoves() {
        var g = new ChessGame();
        assertTrue(g.tryMove("e4", Color.white));
        assertFalse(g.tryMove("e2", Color.white));
        assertFalse(g.tryMove("f2", Color.white));
        assertFalse(g.tryMove("e5", Color.white));
        assertFalse(g.tryMove("e6", Color.white));
        assertFalse(g.tryMove("e1", Color.white));
        assertTrue(g.tryMove("Nc3", Color.white));
        assertFalse(g.tryMove("Na1", Color.white));
        assertFalse(g.tryMove("Nb1", Color.white));
        assertFalse(g.tryMove("Nc2", Color.white));
        assertFalse(g.tryMove("Kd1", Color.white));
        assertFalse(g.tryMove("Ba1", Color.white));
        assertFalse(g.tryMove("Bb2", Color.white));
        assertFalse(g.tryMove("Bc2", Color.white));
        assertFalse(g.tryMove("Bc1", Color.white));


    }

    @Test
    public void capture() {
        var g = new ChessGame();
        assertFalse(g.tryMove("exf3", Color.white));
        assertFalse(g.tryMove("exf7", Color.white));
        assertFalse(g.tryMove("exf7", Color.black));
        assertFalse(g.tryMove("exf3", Color.black));
    }

    @Test
    public void castling() {
        var g = new ChessGame();
        assertFalse(g.tryMove("O-O", Color.white));
        assertFalse(g.tryMove("O-O", Color.black));
        assertFalse(g.tryMove("O-O-O", Color.white));
        assertFalse(g.tryMove("O-O-O", Color.black));
    }

    @Test
    public void blackFirstMoves() {
        var g = new ChessGame();
        assertTrue(g.tryMove("e5", Color.black));
        assertFalse(g.tryMove("e4", Color.black));
        assertTrue(g.tryMove("e6", Color.black));
        assertFalse(g.tryMove("e7", Color.black));
        assertFalse(g.tryMove("e8", Color.black));
        assertTrue(g.tryMove("Nc6", Color.black));
        assertFalse(g.tryMove("Na8", Color.black));
        assertFalse(g.tryMove("Nb8", Color.black));
        assertFalse(g.tryMove("Nc7", Color.black));
        assertFalse(g.tryMove("Kd8", Color.black));
        assertFalse(g.tryMove("Ba8", Color.black));
        assertFalse(g.tryMove("Bb7", Color.black));
        assertFalse(g.tryMove("Bc7", Color.black));
        assertFalse(g.tryMove("Bc8", Color.black));
    }
}
