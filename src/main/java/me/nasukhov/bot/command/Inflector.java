package me.nasukhov.bot.command;

import me.nasukhov.bot.Command;

public class Inflector {
    public Class<? extends Handler> inflect(Command command) {
        if (LearnWordHandler.canHandle(command)) {
            return LearnWordHandler.class;
        }

        if (QuestionHandler.canHandle(command)) {
            return QuestionHandler.class;
        }

        // TODO stub
        return TranslateWordHandler.class;
    }
}
