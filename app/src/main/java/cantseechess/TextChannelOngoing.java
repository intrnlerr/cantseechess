package cantseechess;

import cantseechess.chess.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class TextChannelOngoing implements OngoingGame {
    private final BoardMessageManager boardMessageManager;
    private final GameManager manager;
    private final TextChannel channel;
    private final ChessGame game;
    private final long whitePlayerId;
    private final long blackPlayerId;
    private final Rating whiteRating;
    private final Rating blackRating;
    private long drawSender = -1;
    private boolean isDrawable = false;

    public TextChannelOngoing(BoardMessageManager boardMessageManager, GameManager manager, TextChannel channel, ChessGame game, long whitePlayerId, long blackPlayerId, Rating whiteRating, Rating blackRating) {
        this.boardMessageManager = boardMessageManager;
        this.manager = manager;
        this.channel = channel;
        this.game = game;
        this.whitePlayerId = whitePlayerId;
        this.blackPlayerId = blackPlayerId;
        this.whiteRating = whiteRating;
        this.blackRating = blackRating;

        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("Game Begun!")
                .addField("White", "<@!" + whitePlayerId + "> " + whiteRating, true)
                .addField("Black", "<@!" + blackPlayerId + "> " + blackRating, true);
        //boardChannel.sendMessage("game between <@!" + challenge.challenged + "> and <@!" + challenge.challenger + "> begun!").queue();
        channel.sendMessage(builder.build()).queue();
    }


    @Override
    public void playerMove(User sender, Message message) {
        Color color;
        if (sender.getIdLong() == whitePlayerId) {
            color = Color.white;
        } else if (sender.getIdLong() == blackPlayerId) {
            color = Color.black;
        } else {
            return;
        }
        try {
            game.makeMove(game.getMove(message.getContentRaw(), color));
            message.addReaction("U+2705").queue();
        } catch (IllegalMoveException | IllegalArgumentException e) {
            message.addReaction("U+26D4").queue();
        }
        if ((drawSender != -1 || !isDrawable) && game.isInThreefold()) {
            isDrawable = true;
            channel.sendMessage("Threefold repetition: " +
                    "a draw is now claimable with !draw.").queue();
        }
        var result = game.isGameOver();
        if (result != ChessGame.EndState.NotOver) {
            endGame(result);
        }
    }

    @Override
    public void resign(long player) {
        Color color;
        if (player == whitePlayerId) {
            color = Color.white;
        } else if (player == blackPlayerId) {
            color = Color.black;
        } else {
            return;
        }
        endGame(color == Color.white ? ChessGame.EndState.BlackWins : ChessGame.EndState.WhiteWins);
    }

    @Override
    public void draw(long player) {
        if (isDrawable && (player != drawSender)) {
            endGame(ChessGame.EndState.Draw);
            return;
        }
        drawSender = player;
        isDrawable = true;
        channel.sendMessage("A draw has been offered, claim it with !draw.").queue();
    }

    @Override
    public void endGame(ChessGame.EndState endState) {
        if (endState == ChessGame.EndState.NotOver) {
            return;
        }
        var guild = channel.getGuild();
        channel.putPermissionOverride(guild.retrieveMemberById(whitePlayerId).complete())
                .setDeny(Permission.MESSAGE_WRITE).queue();
        channel.putPermissionOverride(guild.retrieveMemberById(blackPlayerId).complete())
                .setDeny(Permission.MESSAGE_WRITE).queue();

        try {
            boardMessageManager.add(new BoardMessage(channel, game.getPGN()));
        } catch (IncorrectFENException | IllegalMoveException e) {
            e.printStackTrace();
        }

        manager.handleGameEnd(this, endState);
    }

    @Override
    public GuildChannel getChannel() {
        return channel;
    }

    @Override
    public long getWhiteId() {
        return whitePlayerId;
    }

    @Override
    public long getBlackId() {
        return blackPlayerId;
    }

    @Override
    public Rating getWhiteRating() {
        return whiteRating;
    }

    @Override
    public Rating getBlackRating() {
        return blackRating;
    }
}
