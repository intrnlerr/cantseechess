package cantseechess;

import cantseechess.chess.ChessGame;
import cantseechess.chess.Color;
import cantseechess.chess.IllegalMoveException;
import cantseechess.chess.Rating;

public class Player {
    private final String id;
    private final Rating rating;
    private ChessGame currentGame;
    private Color currentColor;
    private Player opponent;

    public Player(ChessGame game, Color color, String id) {
        currentGame = game;
        currentColor = color;
        this.id = id;
        this.rating = new Rating();
    }

    public void resetGameInfo() {
        currentGame = null;
        opponent = null;
        currentColor = null;
    }

    public Player getWhite() {
        if (currentGame == null) {
            return null;
        }
        return currentColor == Color.white ? this : opponent;
    }

    public Player getBlack() {
        if (currentGame == null) {
            return null;
        }
        return currentColor == Color.black ? this : opponent;
    }

    public void setOpponent(Player opponent) {
        this.opponent = opponent;
    }

    public Color getColor() {
        return currentColor;
    }

    public void setColor(Color color) {
        this.currentColor = color;
    }

    public void setGame(ChessGame game, Color color) {
        currentGame = game;
        currentColor = color;
    }

    public void makeMove(String moveStr) throws IllegalMoveException {
        var move = currentGame.getMove(moveStr, currentColor);
        currentGame.makeMove(move);
    }

    public ChessGame.EndState isGameOver() {
        return currentGame.isGameOver();
    }

    public String getId() {
        return id;
    }

    public Rating getRating() {
        return rating;
    }

    public Player getOpponent() {
        return opponent;
    }

    public boolean isPlayingGame() {
        return currentGame != null;
    }
}
