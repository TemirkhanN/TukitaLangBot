package me.nasukhov.bot.command;

import me.nasukhov.bot.Command;

public class TranslateWordHandler implements Handler {
    @Override
    public void handle(Command command) {
        command.channel().sendMessage("Translate word handler responding");
    }
}
