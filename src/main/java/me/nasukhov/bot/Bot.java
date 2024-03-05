package me.nasukhov.bot;

import me.nasukhov.bot.command.*;
import me.nasukhov.study.ProgressRepository;
import me.nasukhov.dictionary.DictionaryRepository;
import me.nasukhov.study.QuestionRepository;

import java.util.HashMap;
import java.util.Map;

public class Bot {
    private final Map<String, Handler> handlers = new HashMap<>();
    private final Inflector handlerInflector;

    public Bot() {
        handlerInflector = new Inflector();

        DictionaryRepository dictionary = new DictionaryRepository();
        ProgressRepository progressRepository = new ProgressRepository();
        QuestionRepository questionRepository = new QuestionRepository();

        handlers.put(TranslateWordHandler.class.getName(), new TranslateWordHandler());
        handlers.put(LearnWordHandler.class.getName(), new LearnWordHandler(dictionary, progressRepository));
        handlers.put(QuestionHandler.class.getName(), new QuestionHandler(questionRepository));
    }

    public String getName() {
        return "TukitaLearner";
    }

    public void handle(Command command) {
        Handler handler = handlers.get(handlerInflector.inflect(command));

        handler.handle(command);
    }
}
