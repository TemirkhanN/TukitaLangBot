package me.nasukhov.bot.command;

import me.nasukhov.bot.Input;
import me.nasukhov.bot.Output;

public interface Handler {
    void handle(Input input, Output output);

    boolean supports(Input command);
}
