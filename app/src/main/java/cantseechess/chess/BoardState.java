package cantseechess.chess;

import cantseechess.stockfish.Analysis;
import net.dv8tion.jda.api.entities.Emote;

import java.util.function.Consumer;

public class BoardState {
    private Analysis analysis;
    private String score;
    private static final String EMPTY_STRING = "N/A";
    private static final String ANALYZING_STRING = "Analyzing...";
    public final Emote[][] board;
    public final String FEN;

    public BoardState(String FEN, Emote[][] board) {
        this.FEN = FEN;
        this.board = board;
    }

    public void setScore(String score) {
        System.out.println(score);
        if (this.score != null) return;
        this.score = score;
    }

    public String getScore() {
        String displayScore;
        if (score != null) displayScore = score;
        else if (analysis != null) displayScore = ANALYZING_STRING;
        else displayScore = EMPTY_STRING;
        return displayScore;
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
