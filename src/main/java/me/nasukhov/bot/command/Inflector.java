package me.nasukhov.bot.command;

import me.nasukhov.bot.Command;

import java.util.Objects;

public class Inflector {
    public String inflect(Command command) {
        if (Objects.equals(command.input(), "/learn")) {
            return LearnWordHandler.class.getName();
        }

        // TODO think about encapsulating canHandle instead of using inflector
        if (Objects.equals(command.input(), "/ask") || command.input().startsWith(QuestionHandler.Id)) {
            return QuestionHandler.class.getName();
        }

        // TODO stub
        return TranslateWordHandler.class.getName();
    }
}
