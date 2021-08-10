package cantseechess;

import cantseechess.chess.BoardGenerator;
import cantseechess.chess.ChessGame;
import cantseechess.chess.IllegalMoveException;
import cantseechess.chess.IncorrectFENException;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.HashMap;

public class BoardMessageManager {
    private final HashMap<Long, BoardMessage> messages = new HashMap<>();

    public void onButtonClick(long messageIdLong, String componentId) {
        var message = messages.get(messageIdLong);
        if (message == null) {
            return;
        }
        switch (componentId) {
            case "First":
                message.first();
                break;
            case "Last":
                message.last();
                break;
            case "Previous":
                message.previous();
                break;
            case "Next":
                message.next();
                break;
            case "Analyze":
                message.startAnalysis();
                break;
            default:
                throw new IllegalStateException("componentId not a known button?");
        }
    }

    public void add(TextChannel channel, ArrayList<ChessGame.Move> moves, String title) throws IncorrectFENException {
        if (BoardGenerator.boardEmotes == null) {
            System.out.println("no emotes!");
            return;
        }
        var msg = new BoardMessage(channel, moves, title);
        messages.put(msg.getMessage().getIdLong(), msg);
    }

    public void add(TextChannel channel, String PGN, String title) throws IncorrectFENException, IllegalMoveException {
        if (BoardGenerator.boardEmotes == null) {
            System.out.println("no emotes!");
            return;
        }
        var msg = new BoardMessage(channel, BoardGenerator.getMoves(PGN), title);
        messages.put(msg.getMessage().getIdLong(), msg);
    }
}
