package me.nasukhov.bot.command;

import me.nasukhov.bot.Input;
import me.nasukhov.bot.Output;

public interface Handler {
    boolean supports(Input input);
    void handle(Input input, Output output);
}
