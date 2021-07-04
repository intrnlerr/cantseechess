package cantseechess;

import cantseechess.chess.ChessGame;
import cantseechess.chess.Color;
import cantseechess.chess.rating.Rating;

public class Player {
    private ChessGame currentGame;
    private Color currentColor;
    private Rating rating;

    public Player(ChessGame game, Color color) {
        currentGame = game;
        currentColor = color;
        this.rating = new Rating();
    }

    public void setGame(ChessGame game, Color color) {
        currentGame = game;
        currentColor = color;
    }

    public void makeMove(String moveStr) {
        var move = currentGame.getMove(moveStr, currentColor);
        if (move.isEmpty()) {
            return;
        }
        currentGame.makeMove(move.get());
    }
}
