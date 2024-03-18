package me.nasukhov.bot;

import me.nasukhov.bot.command.*;

import java.util.HashMap;
import java.util.Map;

public class Bot {
    private final Map<Class<? extends Handler>, Handler> handlers = new HashMap<>();
    private final Inflector handlerInflector;

    public Bot(Inflector inflector) {
        handlerInflector = inflector;
    }

    public void addHandler(Handler handler) {
        handlers.put(handler.getClass(), handler);
    }

    public String getName() {
        return "TukitaLangBot";
    }

    public void handle(Command command) {
        Handler handler = handlers.get(handlerInflector.inflect(command));

        handler.handle(command);
    }
}
