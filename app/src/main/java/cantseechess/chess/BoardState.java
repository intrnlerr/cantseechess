package cantseechess.chess;

import cantseechess.stockfish.Analysis;
import net.dv8tion.jda.api.entities.Emote;

import java.util.function.Consumer;

public class BoardState {
    //TODO change from public
    public Analysis analysis;
    private String score = "N/A";
    private static final String EMPTY_STRING = "N/A";
    private static final String ANALYZING_STRING = "Analyzing...";
    public final Emote[][] board;
    public final String FEN;

    public BoardState(String FEN, Emote[][] board) {
        this.FEN = FEN;
        this.board = board;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getScore() {
        if (!score.equals(EMPTY_STRING) && !score.equals(ANALYZING_STRING)) return score;
        else if (analysis != null) score = ANALYZING_STRING;
        else score = EMPTY_STRING;
        return score;
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
