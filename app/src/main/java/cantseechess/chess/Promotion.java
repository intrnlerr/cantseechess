package cantseechess.chess;

enum Promotion {
    NotPromotion,
    Queen,
    Rook,
    Bishop,
    Knight;

    public static Promotion getFromChar(char c) {
        switch (c) {
            case 'Q':
                return Queen;
            case 'N':
                return Knight;
            case 'R':
                return Rook;
            case 'B':
                return Bishop;
        }
        return NotPromotion;
    }
}
