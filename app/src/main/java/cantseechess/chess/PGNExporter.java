package cantseechess.chess;

import java.util.HashMap;
import java.util.List;

public class PGNExporter {
    private final HashMap<String, String> tags = new HashMap<>();
    public String event;
    public String site;
    public String date;
    public String white;
    public String black;
    public String result;

    public void addTag(String name, String value) {
        tags.put(name, value);
    }

    public String getPGN(List<ChessGame.Move> moves) {
        StringBuilder b = new StringBuilder();
        b.append("[Event \"").append(event).append("\"]\n");
        b.append("[Site \"").append(site).append("\"]\n");
        b.append("[Date \"").append(date).append("\"]\n");
        b.append("[White \"").append(white).append("\"]\n");
        b.append("[Black \"").append(black).append("\"]\n");
        b.append("[Result \"").append(result).append("\"]\n");

        tags.forEach((tagName, tagValue) -> {
            b.append('[');
            b.append(tagName);
            b.append(" \"");
            b.append(tagValue);
            b.append("\"]\n");
        });
        b.append('\n');
        var g = new ChessGame();
        var halfMoves = 0;
        for (var m : moves) {
            if (halfMoves % 2 == 0) {
                b.append(halfMoves / 2 + 1);
                b.append('.');
            }
            var fromPiece = g.getPiece(m.from);
            var fromChar = '!';
            var isCapture = !g.getPiece(m.to).isBlank();
            // FIXME: ambiguous PGNs can be returned!
            if (fromPiece instanceof Blank) {
                throw new IllegalArgumentException("invalid list of moves");
            } else if (fromPiece instanceof Knight) {
                fromChar = 'N';
            } else if (fromPiece instanceof Rook) {
                fromChar = 'R';
            } else if (fromPiece instanceof King) {
                fromChar = 'K';
            } else if (fromPiece instanceof Queen) {
                fromChar = 'Q';
            } else if (fromPiece instanceof Bishop) {
                fromChar = 'B';
            }
            if (!(fromPiece instanceof Pawn)) {
                b.append(fromChar);
            }
            if (isCapture) {
                if (fromPiece instanceof Pawn) {
                    b.append((char) (m.from.getFile() + 'a'));
                }
                b.append('x');
            }
            b.append(m.to).append(' ');
            g.makeMove(m);
            // TODO: annotate checks
            ++halfMoves;
        }
        b.append(result);
        return b.toString();
    }
}
