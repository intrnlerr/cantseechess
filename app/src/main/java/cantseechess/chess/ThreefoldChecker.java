package cantseechess.chess;

import java.util.ArrayList;
import java.util.Random;

class ThreefoldChecker {
    private static class zobrist {
        private static final zobrist INSTANCE = new zobrist();
        private final long[] numbers = new long[781];

        private zobrist() {
            var r = new Random(RANDOM_SEED);
            for (int i = 0; i < numbers.length; ++i) {
                numbers[i] = r.nextLong();
            }
        }

        public static long number(int i) {
            return INSTANCE.numbers[i];
        }
    }

    private final ArrayList<Long> moveHashes = new ArrayList<>();
    private static final long RANDOM_SEED = 12345;
    private static final int PIECE_OFFSET = 64;
    private static final int COLOR_OFFSET = 384;
    private static final int TURN_INDEX = 768;
    private static final int CASTLING_OFFSET = 768;
    private static final int EN_PASSANT_OFFSET = 772;

    private long getPieceHash(Piece p, int file, int rank) {
        int i;
        if (p instanceof Pawn) {
            i = 0;
        } else if (p instanceof Rook) {
            i = PIECE_OFFSET;
        } else if (p instanceof King) {
            i = 2 * PIECE_OFFSET;
        } else if (p instanceof Queen) {
            i = 3 * PIECE_OFFSET;
        } else if (p instanceof Bishop) {
            i = 4 * PIECE_OFFSET;
        } else if (p instanceof Knight) {
            i = 5 * PIECE_OFFSET;
        } else {
            throw new IllegalArgumentException("Blank is not a valid piece");
        }
        i += (p.getColor().ordinal() * COLOR_OFFSET) + (file + (rank * 8));
        return zobrist.number(i);
    }

    long getZobristHash(Piece[][] pieces, Color turn, ChessGame.Castling castling, Position enPassant) {
        long hash = 0;
        for (int file = 0; file < 8; ++file) {
            for (int rank = 0; rank < 8; ++rank) {
                if (!pieces[file][rank].isBlank()) {
                    hash ^= getPieceHash(pieces[file][rank], file, rank);
                }
            }
        }
        if (turn == Color.black) {
            hash ^= zobrist.number(TURN_INDEX);
        }
        if (castling.kingSideWhite) {
            hash ^= zobrist.number(CASTLING_OFFSET);
        }
        if (castling.kingSideBlack) {
            hash ^= zobrist.number(CASTLING_OFFSET + 1);
        }
        if (castling.queenSideWhite) {
            hash ^= zobrist.number(CASTLING_OFFSET + 2);
        }
        if (castling.queenSideBlack) {
            hash ^= zobrist.number(CASTLING_OFFSET + 3);
        }
        if (enPassant != null) {
            hash ^= zobrist.number(enPassant.getFile() + EN_PASSANT_OFFSET);
        }
        return hash;
    }

    int repeats(long hash) {
        int repeats = 0;
        for (var h : moveHashes) {
            if (hash == h) {
                ++repeats;
            }
        }
        return repeats;
    }

    void addHash(long hash) {
        moveHashes.add(hash);
    }

    public void unaddHash() {
        moveHashes.remove(moveHashes.size() - 1);
    }
}
