package cantseechess.storage;

import cantseechess.Player;
import cantseechess.chess.Rating;

import java.util.List;

public interface RatingStorage {
    List<Rating.GameEntry> getGames(Player player);

    void addGame(Player player, Rating.GameEntry entry);

    void shutdown();
}
