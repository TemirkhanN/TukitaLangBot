package me.nasukhov.bot.command;

import me.nasukhov.bot.io.Input;
import me.nasukhov.bot.io.Output;

public interface Handler {
    boolean supports(Input input);
    void handle(Input input, Output output);
}
