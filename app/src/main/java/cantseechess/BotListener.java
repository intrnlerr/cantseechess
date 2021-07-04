package cantseechess;

import cantseechess.chess.Color;
import cantseechess.chess.IllegalMoveException;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class BotListener extends ListenerAdapter {
    private final HashMap<String, Player> games = new HashMap<>();
    private final HashMap<String, Challenge> challenges = new HashMap<>();

    // gets user id from mention
    private String parseMention(String mention) {
        if (mention.matches("<@!?[0-9]+>")) {
            if (mention.charAt(2) == '!') {
                return mention.substring(3, mention.length() - 1);
            } else {
                return mention.substring(2, mention.length() - 1);
            }
        }
        return null;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        var content = event.getMessage().getContentRaw();
        System.out.println(content);
        if (content.startsWith("!")) {
            var args = content.split(" ");
            if (args[0].equals("!challenge")) {
                var challengedId = parseMention(args[1]);
                if (challengedId != null) {
                    System.out.println(challengedId);
                    // hopefully valid user mention
                    if (challenges.containsKey(challengedId)) {
                        event.getChannel().sendMessage("already challenged!").queue();
                        return;
                    }
                    challenges.put(challengedId, new Challenge(event.getAuthor().getId(), challengedId));
                    var action = event.getChannel().sendMessage("challenge created...");
                    action.queue();
                }
            } else if (args[0].equals("!accept")) {
                var challenge = challenges.get(event.getAuthor().getId());
                if (challenge == null) {
                    event.getChannel().sendMessage("no challenge!").queue();
                    return;
                }
                var game = challenge.accept();
                games.put(challenge.challenged, new Player(game, Color.white));
                games.put(challenge.challenger, new Player(game, Color.black));
                challenges.remove(event.getAuthor().getId());
                event.getChannel().sendMessage("game").queue();
            } else if (args[0].equals("!decline")) {
                var challenge = challenges.remove(event.getAuthor().getId());
                if (challenge == null) {
                    event.getChannel().sendMessage("no challenge to decline!").queue();
                    return;
                }
                event.getChannel().sendMessage("challenge declined.").queue();
            } else if (args[0].equals("!sp")) {
                games.put(event.getAuthor().getId(), new SelfPlayer());
            }
        } else if (games.containsKey(event.getAuthor().getId())) {
            var player = games.get(event.getAuthor().getId());
            try {
                player.makeMove(content);
            } catch (IllegalMoveException e) {
                event.getChannel().sendMessage(e.getMessage()).queue();
            }
        }
    }
}
