package me.nasukhov.bot;

import me.nasukhov.bot.command.*;

import java.util.ArrayList;
import java.util.List;

public class Bot {
    private final List<Handler> handlers = new ArrayList<>();

    public void addHandler(Handler handler) {
        handlers.add(handler);
    }

    public String getName() {
        return "TukitaLangBot";
    }

    public void handle(Input command, Output output) {
        for (Handler handler : handlers) {
            if (handler.supports(command)) {
                handler.handle(command, output);

                return;
            }
        }
    }
}
