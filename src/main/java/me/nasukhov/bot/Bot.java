package me.nasukhov.bot;

import me.nasukhov.bot.command.Handler;
import me.nasukhov.bot.command.Inflector;
import me.nasukhov.bot.command.LearnWordHandler;
import me.nasukhov.bot.command.TranslateWordHandler;
import me.nasukhov.bot.study.ProgressRepository;
import me.nasukhov.dictionary.DictionaryRepository;

import java.util.HashMap;
import java.util.Map;

public class Bot {
    private final Map<String, Handler> handlers = new HashMap<>();
    private final Inflector handlerInflector;

    public Bot() {
        handlerInflector = new Inflector();

        DictionaryRepository dictionary = new DictionaryRepository();
        ProgressRepository progressRepository = new ProgressRepository();

        handlers.put(TranslateWordHandler.class.getName(), new TranslateWordHandler());
        handlers.put(LearnWordHandler.class.getName(), new LearnWordHandler(dictionary, progressRepository));
    }

    public String getName() {
        return "TukitaLearner";
    }

    public void handle(Command command) {
        Handler handler = handlers.get(handlerInflector.inflect(command));

        handler.handle(command);
    }
}
