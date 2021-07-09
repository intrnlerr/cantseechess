package cantseechess.stockfish;

import java.io.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

//http://wbec-ridderkerk.nl/html/UCIProtocol.html stockfish stuff
public class Analysis {
    private final BufferedReader stockfishReader;
    private final OutputStreamWriter stockfishWriter;
    //The max amount of time stockfish should calculate the score
    private final int maxTime = 5000;

    private Timer timer = new Timer();
    private Consumer<String> received;
    private String FEN;
    private boolean stop = false;
    //send out an analysis which gets processed by the bot and the bot edits the message containing the analysis.
    //if the (next) or (previous) buttons are clicked, then the Analysis class restarts and looks at those states instead.

    public Analysis(String FEN, Consumer<String> received) throws IOException {
        Process fish = Runtime.getRuntime().exec(this.getClass().getClassLoader().getResource("stockfish.exe").getPath());
        InputStreamReader fishReader = new InputStreamReader(fish.getInputStream());
        stockfishReader = new BufferedReader(fishReader);
        stockfishWriter = new OutputStreamWriter(fish.getOutputStream());

        this.FEN = FEN;
        this.received = received;
        run();
    }


    public void stop() throws IOException {
        send("stop");
        stop = true;
    }

    public void restart(String FEN) throws IOException {
        stop();
        stop = false;
        this.FEN = FEN;
        run();
    }

    //Send string str to stockfish
    private void send(String str) throws IOException{
        stockfishWriter.write(str + "\n");
        stockfishWriter.flush();
    }

    //Runs the timer event, collecting the most recent analysis from stockfish
    private void run() throws IOException{
        send("position fen " + FEN);
        send("go movetime " + maxTime);

        long startTime = System.currentTimeMillis();
        try {
            String line;
            while (!stop && (line = stockfishReader.readLine()) != null && System.currentTimeMillis() - startTime < maxTime) {
                if (line.contains("bestmove")) break;
                if (!line.contains("cp")) continue;
                String cp = getScore(line);
                received.accept(cp);
            }
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
            }
        }
        if (cp == null || cp == 0) return "0.0";
        int multiplier = FEN.split(" ")[1].equals("w") ? 1 : -1;
        double score = Math.round(multiplier * cp / 10.0) / 10.0;
        String symbol = score > 0 ? "+" : "";
        return symbol + score;
    }
}
