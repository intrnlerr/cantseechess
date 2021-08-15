package cantseechess.analysis;

import cantseechess.chess.ChessGame;
import cantseechess.chess.Color;
import cantseechess.chess.IllegalMoveException;
import cantseechess.stockfish.Analysis;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class AnalysisTest {

    @Test
    public void analysis() throws IllegalMoveException, IOException {
        var game = new ChessGame();
        var m1 = game.getMove("e4", Color.white);
        game.makeMove(m1);
        var m2 = game.getMove("e5", Color.black);
        var moves = new ArrayList<ChessGame.Move>();
        moves.add(m1);
        moves.add(m2);
        var str = new ArrayList<String>();
        var a = new Analysis(getClass().getResource("stockfish.exe").getPath(), (s, i) -> str.add(s));
        a.setMoves(moves);
        a.run();
        assertEquals(3, str.size());
    }
}
