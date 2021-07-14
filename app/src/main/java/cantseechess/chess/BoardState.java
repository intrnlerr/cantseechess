package cantseechess.chess;

import cantseechess.stockfish.Analysis;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Emote;

import java.awt.image.BufferedImage;
import java.util.function.Consumer;

public class BoardState {
    //TODO change from public
    public Analysis analysis;
    public String score;
    public final Emote[][] board;
    public final String FEN;

    public BoardState(String FEN, Emote[][] board) {
        this.FEN = FEN;
        this.board = board;
    }

    public void startAnalysis() {
        if (this.analysis != null) return;
        this.analysis = new Analysis(FEN, this::updateScore);
        new Thread(analysis).start();
    }

    private void updateScore(String score) {
        this.score = score;
    }

    public String getAnalysis() {
        return score;
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
