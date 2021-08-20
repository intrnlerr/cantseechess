package cantseechess;

import cantseechess.chess.BoardGenerator;
import cantseechess.chess.ChessGame;
import cantseechess.chess.IllegalMoveException;
import cantseechess.chess.IncorrectFENException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class BoardMessageManager {
    private final BoardGenerator generator = new BoardGenerator();
    private final HashMap<Long, BoardMessage> messages = new HashMap<>();
    private final String stockfishPath;
    private final String emojiGuild;

    public BoardMessageManager(String stockfishPath, String emojiGuild) {
        this.stockfishPath = stockfishPath;
        this.emojiGuild = emojiGuild;
    }

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
        if (generator.noEmotes()) {
            System.out.println("no emotes!");
            return;
        }
        try {
            var msg = new BoardMessage(channel, moves, title, stockfishPath, generator);
            messages.put(msg.getMessage().getIdLong(), msg);
        } catch (IOException e) {
            System.out.println();
        }
    }

    public void add(TextChannel channel, String PGN, String title) throws IncorrectFENException, IllegalMoveException {
        if (generator.noEmotes()) {
            System.out.println("no emotes!");
            return;
        }
        try {
           var msg = new BoardMessage(channel, BoardGenerator.getMoves(PGN), title, stockfishPath, generator);
            messages.put(msg.getMessage().getIdLong(), msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setEmojiGuild(JDA jda) {
        var emoteGuild = jda.getGuildById(emojiGuild);
        if (emoteGuild != null) {
            generator.setBoardEmotes(emoteGuild.getEmotes().toArray(Emote[]::new));
        } else {
            System.out.println("could not get emote guild");
        }
    }
}
