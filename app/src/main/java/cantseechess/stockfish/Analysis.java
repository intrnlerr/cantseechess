package cantseechess.stockfish;

import cantseechess.chess.ChessGame;
import cantseechess.chess.Color;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.function.Consumer;

//http://wbec-ridderkerk.nl/html/UCIProtocol.html stockfish stuff
// TODO: analysis is likely better as a singleton with a single stockfish process
public class Analysis implements Runnable {
    private BufferedReader stockfishReader;
    private OutputStreamWriter stockfishWriter;
    //The max amount of time stockfish should calculate the score
    private final int maxTime = 2000;

    private List<ChessGame.Move> movesToAnalyze;

    private Consumer<String> received;
    private Color currentColor;
    private Process fish;

    //send out an analysis which gets processed by the bot and the bot edits the message containing the analysis.
    //if the (next) or (previous) buttons are clicked, then the Analysis class restarts and looks at those states instead.

    // TODO: allow games with custom FENs to be analyzed
    public Analysis(Consumer<String> received) {
        try {
            var stockUrl = getClass().getClassLoader().getResource("stockfish.exe");
            if (stockUrl == null) {
                throw new NullPointerException("somehow JAR generation did not pack stockfish!!");
            }
            fish = Runtime.getRuntime().exec(stockUrl.getPath());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        InputStreamReader fishReader = new InputStreamReader(fish.getInputStream());
        stockfishReader = new BufferedReader(fishReader);
        stockfishWriter = new OutputStreamWriter(fish.getOutputStream());
        currentColor = Color.white;
        this.received = received;
    }

    public void setMoves(@Nonnull List<ChessGame.Move> moves) {
        movesToAnalyze = moves;
    }

    //Send string str to stockfish
    private void send(String str) {
        try {
            stockfishWriter.write(str + "\n");
            stockfishWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Runs the analysis, getting the analysis once stockfish is done calculating it
    @Override
    public void run() {
        while (true) {
            var position = new StringBuilder("position startpos");
            send(position.toString());

            try {
                analyzeBlocking();
                for (var move : movesToAnalyze) {
                    currentColor = currentColor.other();
                    position.append(' ');
                    position.append(move.getUCIMove());
                    analyzeBlocking();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            // TODO: when done analyzing a game, wait for the next game and analyze that one
        }
    }

    private void analyzeBlocking() throws IOException {
        send("go movetime " + maxTime);
        String line;
        String cp = "0.0";
        while ((line = stockfishReader.readLine()) != null) {
            if (line.contains("bestmove")) break;
            if (!line.contains("cp") && !line.contains("mate")) continue;
            cp = getScore(line);
        }
        received.accept(cp);
    }

    private String getScore(String line) {
        Integer cp = null;
        String[] toParse = line.split(" ");
        for (int i = 0; i < toParse.length; i++) {
            if (toParse[i].equals("cp")) {
                cp = Integer.parseInt(toParse[i + 1]);
                break;
            } else if (toParse[i].equals("mate")) {
                return "#" + Math.abs(Integer.parseInt(toParse[i + 1]));
            }
        }
        if (cp == null || cp == 0) return "0.0";
        int multiplier = currentColor.isWhite() ? 1 : -1;
        double score = Math.round(cp / 10.0) / 10.0 * multiplier;
        String symbol = score > 0 ? "+" : "";
        return symbol + score;
    }
}
