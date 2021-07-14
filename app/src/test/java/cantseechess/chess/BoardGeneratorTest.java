package cantseechess.chess;

import org.junit.Test;

import java.io.IOException;

public class BoardGeneratorTest {
    @Test
    public void makeBoard() throws IncorrectFENException, IllegalMoveException {
        new BoardGenerator().getBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    }
}
