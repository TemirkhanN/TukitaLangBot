package me.nasukhov.bot;

import java.util.Map;

public interface Output {
    void write(String text);
    void write(String text, Map<String, String> replyOptions);
}
