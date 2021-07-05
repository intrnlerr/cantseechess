package cantseechess;

import cantseechess.chess.ChessGame;
import cantseechess.chess.Color;
import cantseechess.chess.IllegalMoveException;

public class SelfPlayer extends Player {
    public SelfPlayer(String id) {
        super(new ChessGame(), Color.white, id);
    }

    @Override
    public void makeMove(String moveStr) throws IllegalMoveException {
        super.makeMove(moveStr);
        setColor(getColor() == Color.white ? Color.black : Color.white);
    }
}
