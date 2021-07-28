package cantseechess.chess;

public enum Color {
    white,
    black;

    public Color other() {
        return this == white ? black : white;
    }

    @Override
    public String toString() {
        return this == white ? "White" : "Black";
    }
}
