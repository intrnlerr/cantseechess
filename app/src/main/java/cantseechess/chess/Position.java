package cantseechess.chess;

import java.util.Objects;

public class Position {
    private int file;
    private int rank;

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

    public Position(int file, int rank) {
        this.rank = rank;
        this.file = file;
    }

    public int getFile() {
        return file;
    }

    public int getRank() {
        return rank;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return rank == position.rank &&
                file == position.file;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rank, file);
    }

    @Override
    public String toString() {
        return (char) (file + 'a') + "" + (char) (rank + '1');
    }
}
