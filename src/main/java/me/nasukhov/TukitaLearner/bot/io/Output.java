package me.nasukhov.TukitaLearner.bot.io;

import java.util.Map;

public interface Output {
    void write(String text);
    void promptChoice(String question, Map<String, String> replyOptions);

    default String mention(String userId) {
        return String.format("<user>%s</user>", userId);
    }
}
