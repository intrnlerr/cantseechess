package cantseechess;

import cantseechess.chess.*;
import cantseechess.stockfish.Analysis;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.Button;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class BoardMessage {
    private Message message;
    private int currIndex;
    private final BoardState[] boardStates;
    private final TextChannel channel;
    private final String embedTitle;
    private String opening = " N/A ";
    public boolean isAnalyzing = false;
    private final Analysis analyzer;

    public BoardMessage(TextChannel channel, List<ChessGame.Move> moves, String embedTitle) throws IncorrectFENException, IOException {
        boardStates = BoardGenerator.getBoard(moves, this::setOpening, null);
        this.channel = channel;
        this.embedTitle = embedTitle;
        currIndex = boardStates.length - 1;
        updateEmbed(currentState());
        analyzer = new Analysis(this::handleAnalysis);
        analyzer.setMoves(moves);
    }

    private void handleAnalysis(String score, int index) {
        boardStates[index].setScore(score);
        if (index == currIndex) {
            updateEmbed(currentState());
        }
    }

    private void setMessage(Message m) {
        this.message = m;
    }

    private void setOpening(String opening) {
        this.opening = opening;
    }

    public Message getMessage() {
        return message;
    }

    public void startAnalysis() {
        if (!isAnalyzing) {
            isAnalyzing = true;
            new Thread(analyzer).start();
        }
    }

    private BoardState currentState() {
        return boardStates[currIndex];
    }

    private void updateEmbed(BoardState board) {
        String score = currentState().getScore();
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(embedTitle)
                .setDescription(board.toString())
                .addField("Score", score + " ", true)
                .addField("Opening", opening, true)
                .setFooter(board.FEN);

        if (message == null) {
            channel.sendMessage(embedBuilder.build())
                    .setActionRow(
                            Button.secondary("First", "First"),
                            Button.secondary("Previous", "Previous"),
                            Button.secondary("Next", "Next").withDisabled(true),
                            Button.secondary("Last", "Last").withDisabled(true),
                            Button.danger("Analyze", "Analyze"))
                    .queue(this::setMessage);
        } else {
            message.editMessage(embedBuilder.build())
                    .setActionRow(updateButtons()).queue();
        }
    }

    //there is probably a better way to do all of this but it's 3:30 am
    private Collection<Button> updateButtons() {
        if (message == null) return Collections.emptyList();

        ArrayList<Button> b = new ArrayList<>();
        boolean isAtEnd;
        if (currIndex == boardStates.length - 1) {
            isAtEnd = true;
        } else if (currIndex == 0) {
            isAtEnd = false;
        } else {
            message.getActionRows().get(0).getButtons().forEach(button -> b.add(button.withDisabled(false)));
            return b;
        }
        b.add(message.getActionRows().get(0).getButtons().get(0).withDisabled(!isAtEnd));
        b.add(message.getActionRows().get(0).getButtons().get(1).withDisabled(!isAtEnd));
        b.add(message.getActionRows().get(0).getButtons().get(2).withDisabled(isAtEnd));
        b.add(message.getActionRows().get(0).getButtons().get(3).withDisabled(isAtEnd));
        b.add(message.getActionRows().get(0).getButtons().get(4).withDisabled(false));
        return b;
    }

    public void first() {
        updateEmbed(boardStates[currIndex = 0]);
    }

    public void previous() {
        if (currIndex == 0)
            updateEmbed(boardStates[currIndex]);
        else
            updateEmbed(boardStates[--currIndex]);
    }

    public void next() {
        if (currIndex == boardStates.length - 1)
            updateEmbed(boardStates[currIndex]);
        else
            updateEmbed(boardStates[++currIndex]);
    }

    public void last() {
        updateEmbed(boardStates[currIndex = boardStates.length - 1]);
    }
}
