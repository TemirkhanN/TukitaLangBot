package me.nasukhov.TukitaLearner.bot.io;

public record Input(String input, Channel channel, User sender) {
    public boolean isDirectCommand(String command) {
        return input.equals("/" + command) || input.startsWith("/" + command + "@");
    }

    @Override
    public String toString() {
        return input;
    }
}
