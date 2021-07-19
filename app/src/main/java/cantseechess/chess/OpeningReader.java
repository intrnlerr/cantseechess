package cantseechess.chess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;
import java.util.TreeMap;

public class OpeningReader {
    //Name, FEN
    private  static final TreeMap<String, String> openings = new TreeMap<>();
    public OpeningReader() {
        if (!openings.isEmpty()) return;
        try {
            for (char i = 'a'; i <= 'e'; i++) {
                URL url = new URL("https://raw.githubusercontent.com/niklasf/chess-openings/master/" + i + ".tsv");
                URLConnection conn = url.openConnection();

                conn.setRequestProperty("X-Requested-With", "Curl");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                String[] info;
                while ((line = reader.readLine()) != null) {
                    info = line.split("\t");
                    if (info.length < 4) continue;
                    openings.put(info[2], info[1]);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Optional<String> getOpening(String FEN) {
        String[] split = FEN.split(" ");
        FEN = "";
        //opening database doesn't have turn number or half-move clock. SAD...
        for (int i = 0; i < 3; i++) {
            FEN += split[i] + " ";
        }
        FEN += split[3];

        String opening = openings.get(FEN);
        return Optional.ofNullable(opening);
    }
}
