package cantseechess;

import java.util.HashMap;
import java.util.TimerTask;

public class CancelChallengeTask extends TimerTask {
    private final HashMap<String, Challenge> challenges;
    private final String id;

    public CancelChallengeTask(HashMap<String, Challenge> challenges, String id) {
        this.challenges = challenges;
        this.id = id;
    }


    @Override
    public void run() {
        challenges.remove(id);
    }
}
