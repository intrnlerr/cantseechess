package cantseechess.stockfish;

import java.io.*;
import java.util.Arrays;

//http://wbec-ridderkerk.nl/html/UCIProtocol.html stockfish stuff
public class Analysis {
    private final BufferedReader stockfishReader;
    private final OutputStreamWriter stockfishWriter;
    //The amount of time in milliseconds stockfish should take to determine analysis
    private final int time = 1000;
    public Analysis() throws IOException {
        Process fish = Runtime.getRuntime().exec(this.getClass().getClassLoader().getResource("stockfish.exe").getPath());
        InputStreamReader fishReader = new InputStreamReader(fish.getInputStream());
        stockfishReader = new BufferedReader(fishReader);
        stockfishWriter = new OutputStreamWriter(fish.getOutputStream());
    }

    private void send(String str) throws IOException{
        stockfishWriter.write(str + "\n");
        stockfishWriter.flush();
    }

    private String receive() throws IOException {
        StringBuffer read = new StringBuffer();
        String line;
        try {
            Thread.sleep(time + 20);
        } catch (Exception e) {

        }
        send("isready");
        while (!(line = stockfishReader.readLine()).equals("readyok")) {
            read.append(line + "\n");
        }
        return read.toString();
    }

    public String analyze(String FEN) throws IOException {

        send("position fen " + FEN);
        send("go movetime " + time);

        String read = receive();

        String[] toParse = read.split("\n");

        Integer cp = null;
        //find centipawn score in the last
        for (int i = 0; i < toParse.length; i++) {
            String[] s = toParse[i].split(" ");
            for (int ii = 0; ii < s.length; ii++) {
                if (s[ii].equals("cp")) {
                    cp = Integer.parseInt(s[ii + 1]);
                    break;
                }
            }
        }
        if (cp == null) return null;
        int multiplier = FEN.split(" ")[1].equals("w") ? 1 : -1;
        double score = Math.round(multiplier * cp/10.0)/10.0;
        String symbol = score > 0 ? "+" : "";
        return symbol + score;
    }
}
