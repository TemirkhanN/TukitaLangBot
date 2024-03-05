package me.nasukhov.bot;

import java.util.Map;

public class Channel {
    public final String id;
    private final Output output;

    public Channel(String id, Output output) {
        this.id = id;
        this.output = output;
    }

    public void sendMessage(String message) {
        output.write(message);
    }

    public void sendQuestion(String question, Map<String, String> possibleReplies) {
        output.write(question, possibleReplies);
    }
}
