package me.nasukhov.bot.command;

import me.nasukhov.bot.Command;

public interface Handler {
    void handle(Command command);

    boolean supports(Command command);
}
