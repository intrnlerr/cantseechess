package cantseechess.chess;

import org.junit.Test;

import static junit.framework.TestCase.*;

public class ChessGameTest {
    private void assertPiece(ChessGame g, String pos, Color color, Class<?> pclass) {
        var p = g.getPiece(new Position(pos));
        assertTrue(pclass.isInstance(p) && p.getColor() == color);
    }

    @Test
    public void constructorTest() {
        var b = new ChessGame();
        assertTrue(b.getPiece(new Position(0, 0)) instanceof Rook);
        assertTrue(b.getPiece(new Position(1, 0)) instanceof Knight);
        assertTrue(b.getPiece(new Position(2, 0)) instanceof Bishop);
        assertTrue(b.getPiece(new Position(3, 0)) instanceof Queen);
        assertTrue(b.getPiece(new Position(4, 0)) instanceof King);
        assertTrue(b.getPiece(new Position(5, 0)) instanceof Bishop);
        assertTrue(b.getPiece(new Position(6, 0)) instanceof Knight);
        assertTrue(b.getPiece(new Position(7, 0)) instanceof Rook);

        for (int file = 0; file < 7; ++file) {
            assertEquals(Color.white, b.getPiece(new Position(file, 0)).getColor());
        }
        //check pawns
        for (int file = 0; file < 7; ++file) {
            assertTrue(b.getPiece(new Position(file, 1)) instanceof Pawn);
            assertEquals(Color.white, b.getPiece(new Position(file, 1)).getColor());
        }

        assertTrue(b.getPiece(new Position(0, 7)) instanceof Rook);
        assertTrue(b.getPiece(new Position(1, 7)) instanceof Knight);
        assertTrue(b.getPiece(new Position(2, 7)) instanceof Bishop);
        assertTrue(b.getPiece(new Position(3, 7)) instanceof Queen);
        assertTrue(b.getPiece(new Position(4, 7)) instanceof King);
        assertTrue(b.getPiece(new Position(5, 7)) instanceof Bishop);
        assertTrue(b.getPiece(new Position(6, 7)) instanceof Knight);
        assertTrue(b.getPiece(new Position(7, 7)) instanceof Rook);

        for (int file = 0; file < 7; ++file) {
            assertEquals(Color.black, b.getPiece(new Position(file, 7)).getColor());
        }
        //check pawns
        for (int file = 0; file < 7; ++file) {
            assertTrue(b.getPiece(new Position(file, 6)) instanceof Pawn);
            assertEquals(Color.black, b.getPiece(new Position(file, 6)).getColor());
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
    public void rook() throws IncorrectFENException {
        var g = new ChessGame("8/1p4r1/8/8/8/8/1R4P1/8 w - - 0 1");

        assertTrue(g.tryMove("Rxb7", Color.white));
        assertTrue(g.tryMove("Rb3", Color.white));
        assertTrue(g.tryMove("Ra2", Color.white));
        assertTrue(g.tryMove("Rb1", Color.white));
        assertTrue(g.tryMove("Rc2", Color.white));
        assertFalse(g.tryMove("Ra1", Color.white));
        assertFalse(g.tryMove("Ra3", Color.white));
        assertFalse(g.tryMove("Rc3", Color.white));
        assertFalse(g.tryMove("Rc1", Color.white));
        assertFalse(g.tryMove("Rg2", Color.white));
        assertFalse(g.tryMove("Rf8", Color.white));
        assertFalse(g.tryMove("Rh6", Color.white));
        assertFalse(g.tryMove("Rf6", Color.white));
        assertFalse(g.tryMove("Rf8", Color.white));
        assertFalse(g.tryMove("Rxg2", Color.white));

        assertTrue(g.tryMove("Rxg2", Color.black));
        assertTrue(g.tryMove("Rg6", Color.black));
        assertTrue(g.tryMove("Rg8", Color.black));
        assertTrue(g.tryMove("Rf7", Color.black));
        assertTrue(g.tryMove("Rh7", Color.black));
        assertFalse(g.tryMove("Ra1", Color.black));
        assertFalse(g.tryMove("Ra3", Color.black));
        assertFalse(g.tryMove("Rc3", Color.black));
        assertFalse(g.tryMove("Rc1", Color.black));
        assertFalse(g.tryMove("Rf8", Color.black));
        assertFalse(g.tryMove("Rh6", Color.black));
        assertFalse(g.tryMove("Rf6", Color.black));
        assertFalse(g.tryMove("Rf8", Color.black));
        assertFalse(g.tryMove("Rxb7", Color.black));
    }

    @Test
    public void queen() throws IncorrectFENException {
        var g = new ChessGame("8/8/2Q2p2/8/8/2P2q2/8/8 w - - 0 1");

        assertTrue(g.tryMove("Qxf6", Color.white));
        assertTrue(g.tryMove("Qxf3", Color.white));
        assertTrue(g.tryMove("Qd6", Color.white));
        assertTrue(g.tryMove("Qb6", Color.white));
        assertTrue(g.tryMove("Qa6", Color.white));
        assertTrue(g.tryMove("Qc5", Color.white));
        assertTrue(g.tryMove("Qc7", Color.white));
        assertTrue(g.tryMove("Qb5", Color.white));
        assertTrue(g.tryMove("Qb7", Color.white));
        assertTrue(g.tryMove("Qd7", Color.white));
        assertFalse(g.tryMove("Qc3", Color.white));
        assertFalse(g.tryMove("Qc2", Color.white));
        assertFalse(g.tryMove("Qe5", Color.white));
        assertFalse(g.tryMove("Qd4", Color.white));
        assertFalse(g.tryMove("Qc6", Color.white));
        assertFalse(g.tryMove("Qe3", Color.white));
        assertFalse(g.tryMove("Qf4", Color.white));
        assertFalse(g.tryMove("Qc6", Color.white));

        assertTrue(g.tryMove("Qxc3", Color.black));
        assertTrue(g.tryMove("Qxc6", Color.black));
        assertTrue(g.tryMove("Qf2", Color.black));
        assertTrue(g.tryMove("Qf4", Color.black));
        assertTrue(g.tryMove("Qe4", Color.black));
        assertTrue(g.tryMove("Qe3", Color.black));
        assertTrue(g.tryMove("Qe2", Color.black));
        assertTrue(g.tryMove("Qg4", Color.black));
        assertTrue(g.tryMove("Qg3", Color.black));
        assertTrue(g.tryMove("Qg2", Color.black));
        assertFalse(g.tryMove("Qf6", Color.black));
        assertFalse(g.tryMove("Qf7", Color.black));
        assertFalse(g.tryMove("Qe6", Color.black));
        assertFalse(g.tryMove("Qg6", Color.black));
        assertFalse(g.tryMove("Qe5", Color.black));
        assertFalse(g.tryMove("Qd4", Color.black));
        assertFalse(g.tryMove("Qb7", Color.black));
        assertFalse(g.tryMove("Qf3", Color.black));
    }

    @Test
    public void castling() throws IncorrectFENException, IllegalMoveException {
        var g = new ChessGame();
        assertFalse(g.tryMove("O-O", Color.white));
        assertFalse(g.tryMove("O-O", Color.black));
        assertFalse(g.tryMove("O-O-O", Color.white));
        assertFalse(g.tryMove("O-O-O", Color.black));

        g = new ChessGame("r3kbnr/pppbqppp/2np4/4p3/4P3/1PN2N2/P1PPBPPP/R1BQK2R w KQkq - 1 6");
        g.makeMove(g.getMove("O-O", Color.white));
        assertPiece(g, "g1", Color.white, King.class);
        assertPiece(g, "f1", Color.white, Rook.class);
        g.makeMove(g.getMove("O-O-O", Color.black));
        assertPiece(g, "c8", Color.black, King.class);
        assertPiece(g, "d8", Color.black, Rook.class);
        assertFalse(g.tryMove("O-O-O", Color.white));
        assertFalse(g.tryMove("O-O", Color.black));

        g = new ChessGame("4k2r/5ppp/8/8/8/8/5PPP/4K2R w Kk - 0 1");
        g.makeMove(g.getMove("O-O", Color.white));
        assertPiece(g, "g1", Color.white, King.class);
        assertPiece(g, "f1", Color.white, Rook.class);
        g.makeMove(g.getMove("O-O", Color.black));
        assertPiece(g, "g8", Color.black, King.class);
        assertPiece(g, "f8", Color.black, Rook.class);
    }

    @Test
    public void invalidNotation() {
        var g = new ChessGame();
        assertFalse(g.tryMove("", Color.white));
        assertFalse(g.tryMove("0", Color.white));
        assertFalse(g.tryMove("a", Color.white));
        assertFalse(g.tryMove("b", Color.white));
        assertFalse(g.tryMove("c", Color.white));
        assertFalse(g.tryMove("d", Color.white));
        assertFalse(g.tryMove("e", Color.white));
        assertFalse(g.tryMove("q", Color.white));
        assertFalse(g.tryMove("R", Color.white));
        assertFalse(g.tryMove("N", Color.white));
        assertFalse(g.tryMove("P", Color.white));
        assertFalse(g.tryMove("Q", Color.white));
        assertFalse(g.tryMove("K", Color.white));
        assertFalse(g.tryMove("R", Color.white));
        assertFalse(g.tryMove("??", Color.white));
    }

    @Test
    public void promotion() throws IncorrectFENException, IllegalMoveException {
        var g = new ChessGame("8/PPPPP3/8/8/8/8/ppppp3/8 w - - 0 1");


        assertTrue(g.tryMove("a1=Q", Color.black));
        assertTrue(g.tryMove("b1=N", Color.black));
        assertTrue(g.tryMove("c1=R", Color.black));
        assertTrue(g.tryMove("d1=B", Color.black));
        assertFalse(g.tryMove("e1=K", Color.black));

        g.makeMove(g.getMove("a8=Q", Color.white));
        assertPiece(g, "a8", Color.white, Queen.class);

        g.makeMove(g.getMove("a1=Q", Color.black));
        assertPiece(g, "a1", Color.black, Queen.class);

        g.makeMove(g.getMove("b8=N", Color.white));
        assertPiece(g, "b8", Color.white, Knight.class);

        g.makeMove(g.getMove("b1=N", Color.black));
        assertPiece(g, "b1", Color.black, Knight.class);

        g.makeMove(g.getMove("c8=R", Color.white));
        assertPiece(g, "c8", Color.white, Rook.class);

        g.makeMove(g.getMove("c1=R", Color.black));
        assertPiece(g, "c1", Color.black, Rook.class);

        g.makeMove(g.getMove("d8=B", Color.white));
        assertPiece(g, "d8", Color.white, Bishop.class);

        g.makeMove(g.getMove("d1=B", Color.black));
        assertPiece(g, "d1", Color.black, Bishop.class);

        assertFalse(g.tryMove("e8=K", Color.white));
    }

    @Test
    public void specificNotation() throws IncorrectFENException {
        var g = new ChessGame("r3r3/6Q1/8/R7/8/2Q3Q1/8/R7 w - - 0 1");
        assertTrue(g.tryMove("Qc3e5", Color.white));
        assertTrue(g.tryMove("R1a3", Color.white));
        assertTrue(g.tryMove("Rec8", Color.black));
    }

    @Test
    public void enPassant() throws IncorrectFENException, IllegalMoveException {
        var g = new ChessGame("k7/8/8/1pPp4/8/8/8/K7 w - b6 0 2");
        assertTrue(g.tryMove("cxb6", Color.white));
        assertFalse(g.tryMove("cxd6", Color.white));
        g = new ChessGame("7k/8/8/8/PpPp4/8/8/K7 b - c3 0 1");
        assertTrue(g.tryMove("bxc3", Color.black));
        assertTrue(g.tryMove("dxc3", Color.black));
        assertFalse(g.tryMove("bxa3", Color.black));
        g = new ChessGame("7k/8/8/8/1p6/8/P7/K7 w - - 0 1");
        var move = g.getMove("a4", Color.white);
        g.makeMove(move);
        assertTrue(g.tryMove("bxa3", Color.black));
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

    @Test
    public void isGameOver() throws IncorrectFENException {
        var g = new ChessGame("rnbqkbnr/pp3Qpp/3p4/2p1p3/2B1P3/8/PPPP1PPP/RNB1K1NR b KQkq - 0 4");
        assertEquals(ChessGame.EndState.WhiteWins, g.isGameOver());
        g = new ChessGame("r1b1k1nr/pppp1ppp/2n5/2b1p3/N3P3/3P4/PPP2qPP/RNBQKB1R w KQkq - 0 6");
        assertEquals(ChessGame.EndState.BlackWins, g.isGameOver());
        g = new ChessGame("8/8/8/8/k7/1q6/8/K7 w - - 0 1");
        assertEquals(ChessGame.EndState.Draw, g.isGameOver());
    }

    @Test
    public void insufficientMaterial() throws IncorrectFENException {
        var g = new ChessGame("8/1k6/8/8/8/8/1K4P1/8 w - - 0 1");
        assertEquals(ChessGame.EndState.NotOver, g.isGameOver());
        g = new ChessGame("8/8/8/2k5/8/2K5/8/8 w - - 0 1");
        assertEquals(ChessGame.EndState.Draw, g.isGameOver());
        g = new ChessGame("8/1k1n4/8/8/8/8/1K6/8 w - - 0 1");
        assertEquals(ChessGame.EndState.Draw, g.isGameOver());
        g = new ChessGame("8/1k6/8/8/8/8/1K1N4/8 w - - 0 1");
        assertEquals(ChessGame.EndState.Draw, g.isGameOver());
        g = new ChessGame("8/1k1b4/8/8/8/8/1K6/8 w - - 0 1");
        assertEquals(ChessGame.EndState.Draw, g.isGameOver());
        g = new ChessGame("8/1k6/8/8/8/8/1K1B4/8 w - - 0 1");
        assertEquals(ChessGame.EndState.Draw, g.isGameOver());
        g = new ChessGame("8/1k6/2b5/8/8/8/1KB5/8 w - - 0 1");
        assertEquals(ChessGame.EndState.Draw, g.isGameOver());
        g = new ChessGame("8/1kb5/8/8/8/8/1KB5/8 w - - 0 1");
        assertEquals(ChessGame.EndState.NotOver, g.isGameOver());
        g = new ChessGame("8/1kbn4/8/8/8/8/1K6/8 w - - 0 1");
        assertEquals(ChessGame.EndState.NotOver, g.isGameOver());
    }
}
