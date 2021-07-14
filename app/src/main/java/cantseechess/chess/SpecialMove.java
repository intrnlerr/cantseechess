package cantseechess.chess;

enum SpecialMove {
    NotSpecial,
    KingsideCastle,
    QueensideCastle,
    EnPassant,
    Queen,
    Rook,
    Bishop,
    Knight;

    public static SpecialMove getPromotionFromChar(char c) {
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
        return NotSpecial;
    }
}
