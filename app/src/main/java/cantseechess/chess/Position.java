package cantseechess.chess;

public class Position {
    private int rank;
    private int file;

    public Position(String position) {
        if (position.length() < 2) {
            throw new IllegalArgumentException();
        }
        // check if the file is valid
        if (position.charAt(0) > 'h' || position.charAt(0) < 'a') {
            throw new IllegalArgumentException();
        }
        // check if the rank is a valid number
        if (position.charAt(1) > '8' || position.charAt(1) < '1') {
            throw new IllegalArgumentException();
        }
        // there might be a better way to do this
        file = position.charAt(0) - 'a';
        rank = position.charAt(1) - '1';
    }

    public Position(int rank, int file) {
        this.rank = rank;
        this.file = file;
    }

    public int getFile() {
        return file;
    }

    public int getRank() {
        return rank;
    }
}
