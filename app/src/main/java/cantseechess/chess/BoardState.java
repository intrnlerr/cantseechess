package cantseechess.chess;

import cantseechess.stockfish.Analysis;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Emote;

import java.awt.image.BufferedImage;
import java.util.function.Consumer;

public class BoardState {
    //TODO change from public
    public Analysis analysis;
    public String score = "N/A";
    public final Emote[][] board;
    public final String FEN;

    public BoardState(String FEN, Emote[][] board) {
        this.FEN = FEN;
        this.board = board;
    }

    public void startAnalysis(Consumer<String> score) {
        if (this.analysis != null) return;
        this.analysis = new Analysis(FEN, score);
        new Thread(analysis).start();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Emote[] b: board) {
            for (Emote emoji: b) {
                builder.append(emoji.getAsMention());
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}
