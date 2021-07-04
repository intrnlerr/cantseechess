package cantseechess;

import cantseechess.chess.ChessGame;
import cantseechess.chess.Color;
import cantseechess.chess.IllegalMoveException;
import cantseechess.chess.Rating;

public class Player {
    private ChessGame currentGame;
    private Color currentColor;
    private Rating rating;

    public Player(ChessGame game, Color color) {
        currentGame = game;
        currentColor = color;
        this.rating = new Rating();
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
}
