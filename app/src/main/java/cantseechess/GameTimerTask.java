package cantseechess;

import cantseechess.chess.ChessGame;
import cantseechess.chess.Color;
import net.dv8tion.jda.api.entities.Guild;

import java.util.TimerTask;

// FIXME: make the clock work with the game refactor
class GameTimerTask extends TimerTask {
    private final BotListener l;
    private final Guild guild;
    private final Player player;
    private Color currentTurn;
    private final int whiteIncrement;
    private final int blackIncrement;
    private int whiteTime;
    private int blackTime;

    public GameTimerTask(BotListener l, Guild guild, Player player, Color currentTurn, int whiteIncrement, int blackIncrement, int whiteTime, int blackTime) {
        this.l = l;
        this.guild = guild;
        this.player = player;
        this.currentTurn = currentTurn;
        this.whiteIncrement = whiteIncrement;
        this.blackIncrement = blackIncrement;
        this.whiteTime = whiteTime;
        this.blackTime = blackTime;
    }

    void onMove() {
        if (currentTurn == Color.white) {
            whiteTime += whiteIncrement;
        } else {
            blackTime += blackIncrement;
        }
        currentTurn = currentTurn.other();
    }

    private void end(Color loserColor) {
        /*
        l.endGame(player,
                loserColor == Color.white ? ChessGame.EndState.BlackWins : ChessGame.EndState.WhiteWins,
                guild);*/
    }

    @Override
    public void run() {
        if (currentTurn == Color.white) {
            if (--whiteTime == 0) {
                // game over
                end(Color.white);
            }
        } else {
            if (--blackTime == 0) {
                end(Color.black);
            }
        }
    }
}
