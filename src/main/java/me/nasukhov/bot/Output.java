package me.nasukhov.bot;

import java.util.Map;

public interface Output {
    void write(String text);
    void promptChoice(String question, Map<String, String> replyOptions);
}
