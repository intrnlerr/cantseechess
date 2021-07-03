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
    public void pawnCapture() throws IncorrectFENException {
        var g = new ChessGame("8/1p2p3/r1R5/8/8/r1R5/1P2P3/8 w - - 0 1");
        assertFalse(g.tryMove("exf3", Color.white));
        assertFalse(g.tryMove("exf7", Color.white));
        assertFalse(g.tryMove("exf7", Color.black));
        assertFalse(g.tryMove("exf3", Color.black));

        assertFalse(g.tryMove("bxc3", Color.white));
        assertTrue(g.tryMove("bxa3", Color.white));

        assertFalse(g.tryMove("bxa6", Color.black));
        assertTrue(g.tryMove("bxc6", Color.black));
    }

    @Test
    public void knightCapture() throws IncorrectFENException {
        var g = new ChessGame("1n6/8/p1P5/5p2/2R1PP2/4NP2/8/8 w - - 0 1");
        assertFalse(g.tryMove("Nxa1", Color.black));
        assertFalse(g.tryMove("Nxa2", Color.black));
        assertFalse(g.tryMove("Nxc2", Color.black));
        assertFalse(g.tryMove("Nxa8", Color.black));
        assertFalse(g.tryMove("Nxa6", Color.black));
        assertTrue(g.tryMove("Nxc6", Color.black));

        assertFalse(g.tryMove("Nxc4", Color.white));
        assertFalse(g.tryMove("Nxe4", Color.white));
        assertFalse(g.tryMove("Nxf4", Color.white));
        assertFalse(g.tryMove("Nxf3", Color.white));
        assertTrue(g.tryMove("Nxf5", Color.white));
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
    public void promotion() throws IncorrectFENException {
        var g = new ChessGame("8/PPPPP3/8/8/8/8/ppppp3/8 w - - 0 1");
        assertTrue(g.tryMove("a8=Q", Color.white));
        assertTrue(g.tryMove("b8=N", Color.white));
        assertTrue(g.tryMove("c8=R", Color.white));
        assertTrue(g.tryMove("d8=B", Color.white));
        assertFalse(g.tryMove("e8=K", Color.white));

        assertTrue(g.tryMove("a1=Q", Color.black));
        assertTrue(g.tryMove("b1=N", Color.black));
        assertTrue(g.tryMove("c1=R", Color.black));
        assertTrue(g.tryMove("d1=B", Color.black));
        assertFalse(g.tryMove("e1=K", Color.black));
    }

    @Test
    public void specificNotation() throws IncorrectFENException {
        var g = new ChessGame("r3r3/6Q1/8/R7/8/2Q3Q1/8/R7 w - - 0 1");
        assertTrue(g.tryMove("Qc3e5", Color.white));
        assertTrue(g.tryMove("R1a3", Color.white));
        assertTrue(g.tryMove("Rec8", Color.black));
    }

    @Test
    public void enPassant() throws IncorrectFENException {
        var g = new ChessGame("k7/8/8/1pP5/8/8/8/K7 w - b6 0 2");
        assertTrue(g.tryMove("cxb6", Color.white));
        // TODO: add more en passant cases
    }

    @Test
    public void inCheck() throws IncorrectFENException {
        var g = new ChessGame("7b/8/k7/6B1/8/1q6/8/K7 w - - 0 1");
        assertFalse(g.tryMove("Bh4", Color.white));
        assertFalse(g.tryMove("Bh6", Color.white));
        assertFalse(g.tryMove("Bc1", Color.white));
        assertFalse(g.tryMove("Be7", Color.white));
        assertFalse(g.tryMove("Kb1", Color.white));
        assertFalse(g.tryMove("Ka2", Color.white));
        assertFalse(g.tryMove("Bb2", Color.white));
        assertTrue(g.tryMove("Bf6", Color.white));
    }

    @Test
    public void pawnMoveTwo() throws IncorrectFENException {
        var g = new ChessGame("8/3pppp1/3Br3/6p1/1P6/3Rb3/1PPPP3/8 w - - 0 1");
        assertFalse("square blocked", g.tryMove("b4", Color.white));
        assertFalse("pawn is not on correct rank to move twice", g.tryMove("b6", Color.white));
        assertTrue(g.tryMove("c4", Color.white));
        assertFalse("path is blocked by white piece", g.tryMove("d4", Color.white));
        assertFalse("path is blocked by black piece", g.tryMove("e4", Color.white));

        assertFalse("square blocked", g.tryMove("g5", Color.black));
        assertFalse("pawn is not on correct rank to move twice", g.tryMove("g3", Color.black));
        assertTrue(g.tryMove("f5", Color.black));
        assertFalse("path is blocked by black piece", g.tryMove("e5", Color.black));
        assertFalse("path is blocked by white piece", g.tryMove("d5", Color.black));
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
        assertFalse(g.tryMove("Qd1", Color.white));
        assertFalse(g.tryMove("Qd2", Color.white));
        assertFalse(g.tryMove("Qe1", Color.white));
        assertFalse(g.tryMove("Qc1", Color.white));
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
