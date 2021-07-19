package cantseechess;

import cantseechess.chess.BoardGenerator;
import cantseechess.chess.BoardState;
import cantseechess.chess.IllegalMoveException;
import cantseechess.chess.IncorrectFENException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;

public class BoardMessage {
    private Message message;
    private String score = "N/A";
    private int currIndex;
    private final BoardState[] BOARD_STATES;
    private final TextChannel channel;
    private static final String EMBED_TITLE = "Chess Game";
    private String opening = " N/A ";
    public boolean doAnalysis = false;

    public BoardMessage(TextChannel channel, String PGN) throws IncorrectFENException, IllegalMoveException {
        BOARD_STATES = BoardGenerator.getBoard(PGN, Optional.empty(), this::setOpening);
        this.channel = channel;
        currIndex = BOARD_STATES.length - 1;
        updateEmbed(currentState());
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
        BoardState state = currentState();
        Consumer<String> c = (s -> {
            state.setScore(s);
            if (currentState() == state) updateEmbed(currentState());
        });
        state.startAnalysis(c);
    }

    private BoardState currentState() {
        return BOARD_STATES[currIndex];
    }

    private void updateEmbed(BoardState board) {
        if (doAnalysis) startAnalysis();

        score = currentState().getScore();
        //TODO put players names in
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(EMBED_TITLE)
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
        if (currIndex == BOARD_STATES.length - 1) {
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
        updateEmbed(BOARD_STATES[currIndex = 0]);
    }

    public void previous() {
        if (currIndex == 0)
            updateEmbed(BOARD_STATES[currIndex]);
        else
            updateEmbed(BOARD_STATES[--currIndex]);
    }

    public void next() {
        if (currIndex == BOARD_STATES.length - 1)
            updateEmbed(BOARD_STATES[currIndex]);
        else
            updateEmbed(BOARD_STATES[++currIndex]);
    }

    public void last() {
        updateEmbed(BOARD_STATES[currIndex = BOARD_STATES.length - 1]);
    }
}
