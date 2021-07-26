package cantseechess;

import cantseechess.chess.ChessGame;
import cantseechess.chess.Color;

import java.util.TimerTask;

class ChessClockTask extends TimerTask {
    private final OngoingGame game;
    private final int increment;
    private boolean cancelling;
    private int cancelTimer;
    private Color currentTurn;
    private int whiteTime;
    private int blackTime;

    public ChessClockTask(OngoingGame game, int whiteTime, int blackTime, int increment) {
        this.game = game;
        this.whiteTime = whiteTime;
        this.blackTime = blackTime;
        this.increment = increment;
        cancelling = true;
        cancelTimer = 15;
    }

    void onMove() {
        if (currentTurn == Color.white) {
            whiteTime += increment;
        } else {
            blackTime += increment;
        }
        currentTurn = currentTurn.other();
    }

    private void end(Color loserColor) {
        this.cancel();
        game.endGame(loserColor == Color.white ? ChessGame.EndState.BlackWins : ChessGame.EndState.WhiteWins);
    }

    @Override
    public void run() {
        if (cancelling) {
            if (--cancelTimer == 0) {
                this.cancel();
                game.cancelGame();
            }
            return;
        }
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

    public void stopCancel() {
        cancelling = false;
    }
}
