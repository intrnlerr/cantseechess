package cantseechess.chess;

import cantseechess.chess.Position;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class PositionTest {
    @Test
    public void testConstructor() {
        assertThrows(IllegalArgumentException.class, () -> new Position("a0"));
        assertThrows(IllegalArgumentException.class, () -> new Position("q1"));
        assertThrows(IllegalArgumentException.class, () -> new Position("a9"));
        assertThrows(IllegalArgumentException.class, () -> new Position("hi"));
        assertThrows(IllegalArgumentException.class, () -> new Position(""));

        var p = new Position("a1");
        assertEquals(0, p.getFile());
        assertEquals(0, p.getRank());

        p = new Position("b7");
        assertEquals(1, p.getFile());
        assertEquals(6, p.getRank());
    }
}
