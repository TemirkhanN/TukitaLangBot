package me.nasukhov.bot;

import java.util.Map;

public class Channel {
    public final String id;
    private final Output output;

    private final boolean isPublic;

    public Channel(String id, Output output) {
        this.id = id;
        this.output = output;
        this.isPublic = true;
    }

    public Channel(String id, Output output, boolean isPublic) {
        this.id = id;
        this.output = output;
        this.isPublic = isPublic;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void sendMessage(String message) {
        output.write(message);
    }

    public void sendQuestion(String question, Map<String, String> possibleReplies) {
        output.write(question, possibleReplies);
    }
}
