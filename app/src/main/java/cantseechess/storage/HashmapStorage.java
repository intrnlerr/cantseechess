package cantseechess.storage;

import cantseechess.Player;
import cantseechess.chess.Rating;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HashmapStorage implements RatingStorage {
    private HashMap<String, ArrayList<Rating.GameEntry>> entries = new HashMap<>();

    @Override
    public List<Rating.GameEntry> getGames(Player player) {
        return entries.get(player.getId());
    }

    @Override
    public void addGame(Player player, Rating.GameEntry entry) {
        var arr = entries.computeIfAbsent(player.getId(), k -> new ArrayList<>());
        arr.add(entry);
    }
}
