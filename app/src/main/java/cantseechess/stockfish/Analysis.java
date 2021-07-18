package cantseechess.stockfish;

import java.io.*;
import java.util.function.Consumer;

//http://wbec-ridderkerk.nl/html/UCIProtocol.html stockfish stuff
public class Analysis implements Runnable {
    private BufferedReader stockfishReader;
    private OutputStreamWriter stockfishWriter;
    //The max amount of time stockfish should calculate the score
    private final int maxTime = 2000;

    private Consumer<String> received;
    private String FEN;
    private Process fish;
    //send out an analysis which gets processed by the bot and the bot edits the message containing the analysis.
    //if the (next) or (previous) buttons are clicked, then the Analysis class restarts and looks at those states instead.

    public Analysis(String FEN, Consumer<String> received) {
        try {
            fish = Runtime.getRuntime().exec(this.getClass().getClassLoader().getResource("stockfish.exe").getPath());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        InputStreamReader fishReader = new InputStreamReader(fish.getInputStream());
        stockfishReader = new BufferedReader(fishReader);
        stockfishWriter = new OutputStreamWriter(fish.getOutputStream());
        this.FEN = FEN;
        this.received = received;
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
        send("position fen " + FEN);
        send("go movetime " + maxTime);

        try {
            String line;
            String cp = "0.0";
            while ((line = stockfishReader.readLine()) != null) {
                if (line.contains("bestmove")) break;
                if (!line.contains("cp") && !line.contains("mate")) continue;
                cp = getScore(line);
            }
            received.accept(cp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getScore(String line) {
        Integer cp = null;
        String[] toParse = line.split(" ");
        for (int i = 0; i < toParse.length; i++) {
            if (toParse[i].equals("cp")) {
                cp = Integer.parseInt(toParse[i + 1]);
                break;
            } else if (toParse[i].equals("mate")) {
                return "#"+Math.abs(Integer.parseInt(toParse[i+1]));
            }
        }
        if (cp == null || cp == 0) return "0.0";
        int multiplier = FEN.split(" ")[1].equals("w") ? 1 : -1;
        double score = Math.round(cp / 10.0) / 10.0 * multiplier;
        String symbol = score > 0 ? "+" : "";
        return symbol + score;
    }
}
