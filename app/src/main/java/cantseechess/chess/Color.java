package cantseechess.chess;

public enum Color {
    white,
    black;

    public Color other() {
        return this == white ? black : white;
    }
}
