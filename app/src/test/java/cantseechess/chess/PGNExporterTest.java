package cantseechess.chess;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class PGNExporterTest {
    @Test
    public void noMoves() {
        var e = new PGNExporter();
        e.event = "hi";
        e.site = "Hell, MI USA";
        e.date = "1973.09.11";
        e.white = "test";
        e.black = "test";
        e.result = "";
        assertEquals("[Event \"hi\"]\n" +
                "[Site \"Hell, MI USA\"]\n" +
                "[Date \"1973.09.11\"]\n" +
                "[White \"test\"]\n" +
                "[Black \"test\"]\n" +
                "[Result \"\"]\n\n", e.getPGN(new ArrayList<>()));
    }

    @Test
    public void scandinavian() {
        var e = new PGNExporter();
        e.event = "hi";
        e.site = "Hell, MI USA";
        e.date = "1973.09.11";
        e.white = "test";
        e.black = "test";
        e.result = "1-0";
        var moves = new ArrayList<ChessGame.Move>();
        moves.add(new ChessGame.Move(new Position("e2"), new Position("e4")));
        moves.add(new ChessGame.Move(new Position("d7"), new Position("d5")));
        moves.add(new ChessGame.Move(new Position("e4"), new Position("d5")));
        moves.add(new ChessGame.Move(new Position("d8"), new Position("d5")));
        assertEquals("[Event \"hi\"]\n" +
                "[Site \"Hell, MI USA\"]\n" +
                "[Date \"1973.09.11\"]\n" +
                "[White \"test\"]\n" +
                "[Black \"test\"]\n" +
                "[Result \"1-0\"]\n\n" +
                "1.e4 d5 2.exd5 Qxd5 1-0", e.getPGN((moves)));
    }
}
