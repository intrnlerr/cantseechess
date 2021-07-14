package cantseechess.chess;

import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Emote;

import java.awt.image.BufferedImage;

public class BoardState {
    //TODO change from public
    //public Analysis analysis;
    public final Emote[][] board;
    public final String FEN;
    public BoardState(String FEN, Emote[][] board/*, Consumer<String> s*/) {
        //this.analysis = new Analysis(FEN, s);
        this.FEN = FEN;
        this.board = board;
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
