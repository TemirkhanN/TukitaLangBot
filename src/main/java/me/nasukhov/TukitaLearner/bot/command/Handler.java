package me.nasukhov.TukitaLearner.bot.command;

import me.nasukhov.TukitaLearner.bot.io.Input;
import me.nasukhov.TukitaLearner.bot.io.Output;

public interface Handler {
    boolean supports(Input input);
    void handle(Input input, Output output);
}
