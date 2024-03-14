package me.nasukhov.bot.command;

import me.nasukhov.bot.Command;

public class Inflector {
    public Class<? extends Handler> inflect(Command command) {
        if (command.isDirectCommand("learn")) {
            return LearnWordHandler.class;
        }

        // TODO think about encapsulating canHandle instead of using inflector
        if (command.isDirectCommand("ask") || command.input().startsWith(QuestionHandler.Id)) {
            return QuestionHandler.class;
        }

        // TODO stub
        return TranslateWordHandler.class;
    }
}
