package me.nasukhov.bot;

import me.nasukhov.bot.command.Handler;
import me.nasukhov.bot.command.Inflector;
import me.nasukhov.bot.command.TranslateWordHandler;

import java.util.HashMap;
import java.util.Map;

public class Bot {
    private Map<String, Handler> handlers = new HashMap<>();
    private final Inflector handlerInflector;

    public Bot() {
        handlerInflector = new Inflector();
        handlers.put(TranslateWordHandler.class.getName(), new TranslateWordHandler());
    }

    public String getName() {
        return "TukitaLearner";
    }

    public void handle(Command command) {
        Handler handler = handlers.get(handlerInflector.inflect(command));

        handler.handle(command);
    }
}
