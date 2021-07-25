package cantseechess;

import java.util.ArrayDeque;
import java.util.Optional;

public class GuildBoardData {
    private final ArrayDeque<Long> availableChannels = new ArrayDeque<>();
    private final ArrayDeque<Challenge> queuedChallenges = new ArrayDeque<>();

    public Long pollChannel() {
        return availableChannels.poll();
    }

    public Optional<Challenge> returnChannel(long channel) {
        var chal = queuedChallenges.poll();
        if (chal == null) {
            availableChannels.add(channel);
            return Optional.empty();
        }
        return Optional.of(chal);
    }

    public void dropChannel(long channel) {
        availableChannels.remove(channel);
    }

    public boolean isAvailableBoard(long channel) {
        return availableChannels.contains(channel);
    }

    public void addChannel(long channelId) {
        availableChannels.add(channelId);
    }

    public void queueChallenge(Challenge challenge) {
        queuedChallenges.add(challenge);
    }
}
