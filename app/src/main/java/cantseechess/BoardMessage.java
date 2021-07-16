package cantseechess;

import cantseechess.chess.*;
import cantseechess.stockfish.Analysis;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.Button;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.function.Consumer;



public class BoardMessage {
    private Message message;
    private long lastUpdateTime = 0;
    private String score = "N/A";
    private int currIndex;
    private static final int UPDATE_TIME = 2000;
    private final BoardState[] BOARD_STATES;
    private final TextChannel channel;
    private static final String EMBED_TITLE = "Chess Game";
    private String opening = " N/A ";
    public boolean doAnalysis = false;

    public BoardMessage(TextChannel channel, String PGN) throws IncorrectFENException, IllegalMoveException {
        BOARD_STATES = BoardGenerator.getBoard(PGN, Optional.empty(), this::setOpening);
        this.channel = channel;
        currIndex = BOARD_STATES.length-1;
        updateEmbed(BOARD_STATES[currIndex]);
    }

    private void setMessage(Message m) {
        this.message = m;
    }
    private void setOpening(String opening) {
        System.out.println("Setting opening to " + opening);
        this.opening = opening;
    }
    public Message getMessage() {
        return message;
    }

    public void startAnalysis() {
        Consumer<String> c = (s -> {
            BOARD_STATES[currIndex].score = s;
            updateEmbed(BOARD_STATES[currIndex]);
        });
        BOARD_STATES[currIndex].startAnalysis(c);
    }

    public void updateEmbed() {
        updateEmbed(BOARD_STATES[currIndex]);
    }

    private void updateEmbed(BoardState board) {
        if (System.currentTimeMillis() - lastUpdateTime < UPDATE_TIME) {
            return;
        }
        if (doAnalysis) startAnalysis();
        lastUpdateTime = System.currentTimeMillis();

        score = BOARD_STATES[currIndex].score;
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
        }
        else {
            message.editMessage(embedBuilder.build())
                    .setActionRow(updateButtons()).queue();
        }
    }

    //there is probably a better way to do all of this but it's 3:30 am
    private Collection<Button> updateButtons() {
        if (message == null) return Collections.EMPTY_LIST;

        ArrayList<Button> b = new ArrayList<>();
        boolean bool;
        if (currIndex == BOARD_STATES.length-1) {
            bool = true;
        }
        else if (currIndex == 0) {
            bool = false;
        } else {
            message.getActionRows().get(0).getButtons().forEach(button -> b.add(button.withDisabled(false)));
            return b;
        }
        b.add(message.getActionRows().get(0).getButtons().get(0).withDisabled(false == bool));
        b.add(message.getActionRows().get(0).getButtons().get(1).withDisabled(false == bool));
        b.add(message.getActionRows().get(0).getButtons().get(2).withDisabled(true == bool));
        b.add(message.getActionRows().get(0).getButtons().get(3).withDisabled(true == bool));
        b.add(message.getActionRows().get(0).getButtons().get(4).withDisabled(false));
        return b;
    }

    //TODO make it so you cant click previous or next if currIndex is too low or high
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
        if (currIndex == BOARD_STATES.length-1)
            updateEmbed(BOARD_STATES[currIndex]);
        else
            updateEmbed(BOARD_STATES[++currIndex]);
    }

    public void last() {
        updateEmbed(BOARD_STATES[currIndex = BOARD_STATES.length-1]);
    }


}
