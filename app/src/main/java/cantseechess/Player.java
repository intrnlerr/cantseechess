package cantseechess;

import cantseechess.chess.ChessGame;
import cantseechess.chess.Color;

public class Player {
    private ChessGame currentGame;
    private Color currentColor;

    public Player(ChessGame game, Color color) {
        currentGame = game;
        currentColor = color;
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
