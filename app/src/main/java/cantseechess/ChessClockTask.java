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

    public void intervalCheck(Color turn, int seconds) {
        if (seconds == 10) {
            game.sendClockSeconds(turn, seconds);
        } else if (seconds == 30) {
            game.sendClockSeconds(turn, seconds);
        }
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
            --whiteTime;
            if (whiteTime == 0) {
                // game over
                end(Color.white);
            } else {
                intervalCheck(Color.white, whiteTime);
            }
        } else {
            --blackTime;
            if (blackTime == 0) {
                end(Color.black);
            } else {
                intervalCheck(Color.black, blackTime);
            }
        }
    }

    public void stopCancel() {
        cancelling = false;
    }

    public boolean isCancelling() {
        return cancelling;
    }

    public void resetCancel() {
        cancelTimer = 15;
    }
}
