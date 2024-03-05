package me.nasukhov.bot;

public record Command(String input, Channel channel, User sender) {
    public void reply(String text) {
        channel.sendMessage(text);
    }
}
