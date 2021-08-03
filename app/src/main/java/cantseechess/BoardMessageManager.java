package cantseechess;

import java.util.HashMap;

public class BoardMessageManager {
    private final HashMap<Long, BoardMessage> messages = new HashMap<>();

    public void onButtonClick(long messageIdLong, String componentId) {
        var message = messages.get(messageIdLong);
        if (message == null) {
            return;
        }
        switch (componentId) {
            case "First":
                message.first();
                break;
            case "Last":
                message.last();
                break;
            case "Previous":
                message.previous();
                break;
            case "Next":
                message.next();
                break;
            case "Analyze":
                message.doAnalysis = !message.doAnalysis;
                message.startAnalysis();
                break;
            default:
                throw new IllegalStateException("componentId not a known button?");
        }
    }

    public void add(BoardMessage msg) {
        messages.put(msg.getMessage().getIdLong(), msg);
    }
}
