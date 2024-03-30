package me.nasukhov.bot;

public record Input(String input, Channel channel, User sender) {
    public boolean isDirectCommand(String command) {
        return input.equals("/" + command) || input.startsWith("/" + command + "@");
    }
}
