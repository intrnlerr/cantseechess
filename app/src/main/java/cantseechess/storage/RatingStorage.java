package cantseechess.storage;

import cantseechess.chess.Rating;

import java.util.List;

public interface RatingStorage {
    List<Rating.GameEntry> getGames(long playerId);

    void addGame(long playerId, Rating.GameEntry entry);

    void shutdown();

    Rating getRating(long id);
}
