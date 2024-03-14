package me.nasukhov;

import me.nasukhov.bot.Bot;
import me.nasukhov.bot.bridge.Telegram;
import me.nasukhov.bot.command.Inflector;
import me.nasukhov.bot.command.LearnWordHandler;
import me.nasukhov.bot.command.QuestionHandler;
import me.nasukhov.bot.command.TranslateWordHandler;
import me.nasukhov.dictionary.DictionaryRepository;
import me.nasukhov.study.ProgressRepository;
import me.nasukhov.study.QuestionRepository;

import java.util.HashMap;

final public class ServiceLocator {
    private HashMap<String, Object> instances = new HashMap<>();
    public <T> T locate(Class<T> serviceId) {
        if (serviceId.equals(Bot.class)) {
            return (T) bot();
        }

        if (serviceId.equals(Inflector.class)) {
            return (T) inflector();
        }

        if (serviceId.equals(TranslateWordHandler.class)) {
            return (T) translateWordHandler();
        }

        if (serviceId.equals(LearnWordHandler.class)) {
            return (T) learnWordHandler();
        }

        if (serviceId.equals(QuestionHandler.class)) {
            return (T) questionHandler();
        }

        if (serviceId.equals(DictionaryRepository.class)) {
            return (T) dictionaryRepository();
        }

        if (serviceId.equals(ProgressRepository.class)) {
            return (T) progressRepository();
        }

        if (serviceId.equals(QuestionRepository.class)) {
            return (T) questionRepository();
        }

        if (serviceId.equals(Telegram.class)) {
            return (T) telegramBot();
        }

        throw new RuntimeException("Unknown service requested" + serviceId);
    }

    private Bot bot() {
        String key = Bot.class.getCanonicalName();
        if (!instances.containsKey(key)) {
            Bot declaration =new Bot(locate(Inflector.class));
            declaration.addHandler(locate(TranslateWordHandler.class));
            declaration.addHandler(locate(LearnWordHandler.class));
            declaration.addHandler(locate(QuestionHandler.class));

            instances.put(key, declaration);
        }

        return (Bot) instances.get(key);
    }

    private Inflector inflector() {
        return new Inflector();
    }

    private TranslateWordHandler translateWordHandler() {
        return new TranslateWordHandler();
    }

    private LearnWordHandler learnWordHandler() {
        return new LearnWordHandler(locate(DictionaryRepository.class), locate(ProgressRepository.class));
    }

    private QuestionHandler questionHandler() {
        return new QuestionHandler(locate(QuestionRepository.class));
    }

    private DictionaryRepository dictionaryRepository() {
        String key = DictionaryRepository.class.getCanonicalName();
        if (!instances.containsKey(key)) {
            instances.put(key, new DictionaryRepository());
        }

        return (DictionaryRepository) instances.get(key);
    }

    private ProgressRepository progressRepository() {
        String key = ProgressRepository.class.getCanonicalName();
        if (!instances.containsKey(key)) {
            instances.put(key, new ProgressRepository());
        }

        return (ProgressRepository) instances.get(key);
    }

    private QuestionRepository questionRepository() {
        String key = QuestionRepository.class.getCanonicalName();
        if (!instances.containsKey(key)) {
            instances.put(key, new QuestionRepository());
        }

        return (QuestionRepository) instances.get(key);
    }

    private Telegram telegramBot() {
        String botToken = System.getenv("TG_BOT_TOKEN");

        return new Telegram(botToken, locate(Bot.class));
    }
}
