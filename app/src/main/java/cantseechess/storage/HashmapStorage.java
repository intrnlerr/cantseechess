package cantseechess.storage;

import cantseechess.Player;
import cantseechess.chess.Rating;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HashmapStorage implements RatingStorage {
    private HashMap<String, ArrayList<Rating.GameEntry>> entries = new HashMap<>();

    public HashmapStorage() {
        // try read from file
        try {
            var f = new DataInputStream(new FileInputStream("ratings"));

            while (f.available() > 0) {
                var id = f.readUTF();
                var n_entries = f.readInt();
                var list = new ArrayList<Rating.GameEntry>();
                entries.put(id, list);
                for (int i = 0; i < n_entries; ++i) {
                    var rating = f.readDouble();
                    var deviation = f.readDouble();
                    var result = f.readDouble();
                    list.add(new Rating.GameEntry(rating, deviation, result));
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("no ratings file.");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    @Override
    public List<Rating.GameEntry> getGames(Player player) {
        return entries.get(player.getId());
    }

    @Override
    public void addGame(Player player, Rating.GameEntry entry) {
        var arr = entries.computeIfAbsent(player.getId(), k -> new ArrayList<>());
        arr.add(entry);
    }

    @Override
    public void shutdown() {
        // save to file
        try {
            var out = new DataOutputStream(new FileOutputStream("ratings"));
            entries.forEach((id, games) -> {
                try {
                    out.writeUTF(id);
                    out.writeInt(games.size());
                    for (var game : games) {
                        out.writeDouble(game.rating);
                        out.writeDouble(game.deviation);
                        out.writeDouble(game.result);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (FileNotFoundException e) {
            System.out.println("ratings could not opened for writing!");
        }
    }
}
