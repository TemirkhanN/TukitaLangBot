package me.nasukhov.bot;

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
}
