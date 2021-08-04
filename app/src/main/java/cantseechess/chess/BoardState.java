package cantseechess.chess;

import net.dv8tion.jda.api.entities.Emote;

public class BoardState {
    private String score = "??";
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
        return score;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Emote[] b : board) {
            for (Emote emoji : b) {
                builder.append(emoji.getAsMention());
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}
