package cantseechess;
import cantseechess.Color;
public class ChessGame {
    private Piece[][] board_pieces = new Piece[8][8];
    private Color turnColor = Color.white;
    //Number of half moves since last capture/pawn move
    private int halfmoveClock = 0;
    private int moves = 1;
    //The square which player turnColor can en passant to
    private Position enPassantSquare = null;
    //Availability to castle
    private String castling = "KQkq";
    private final String START_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    public ChessGame() {
        try {
            importFEN(START_FEN);
        } catch (IncorrectFENException e) {
            System.err.println("START_FEN was somehow wrong");
        }
    }
    //Import chess game from fen
    public ChessGame(String FEN) throws IncorrectFENException{
        importFEN(FEN);
    }

    //Return a board position from FEN
    private void importFEN(String FEN) throws IncorrectFENException {
        try {
            String[] content = FEN.split(" ");
            String[] boardContent = content[0].split("/");
            //TODO set board_pieces to the board content in the FEN
            //The color of the player whose turn it is
            turnColor = content[1].equalsIgnoreCase("w") ? Color.white : Color.black;
            castling = content[2];
            enPassantSquare = new Position(content[3]);
            halfmoveClock = Integer.parseInt(content[4]);
            moves = Integer.parseInt(content[5]);
        } catch (Exception e) {
            throw new IncorrectFENException(e);
        }

    }
}
