package me.nasukhov;

import me.nasukhov.bot.Bot;
import me.nasukhov.bot.bridge.Telegram;
import me.nasukhov.bot.command.LearnWordHandler;
import me.nasukhov.bot.command.QuestionHandler;
import me.nasukhov.bot.command.TranslateWordHandler;
import me.nasukhov.db.Connection;
import me.nasukhov.dictionary.DictionaryRepository;
import me.nasukhov.study.ProgressRepository;
import me.nasukhov.study.QuestionRepository;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

final class SharedProvider<T> implements Supplier<T>{
    private final Supplier<T> initializer;

    private T instance;

    SharedProvider(Supplier<T> initializer) {
        this.initializer = initializer;
    }

    @Override
    @NotNull
    public T get() {
        if (instance == null) {
            instance = initializer.get();
        }

        return instance;
    }
}

public class ServiceLocator {
    private final Map<Class<?>, Supplier<?>> initializers = new HashMap<>();

    private boolean resolved = false;

    public ServiceLocator() {
        initializers.put(Connection.class, new SharedProvider<>(this::connection));
        initializers.put(DictionaryRepository.class, new SharedProvider<>(this::dictionaryRepository));
        initializers.put(ProgressRepository.class, new SharedProvider<>(this::progressRepository));
        initializers.put(QuestionRepository.class, new SharedProvider<>(this::questionRepository));
        initializers.put(Telegram.class, new SharedProvider<>(this::telegramBot));
        initializers.put(Bot.class, new SharedProvider<>(this::bot));
        initializers.put(TranslateWordHandler.class, TranslateWordHandler::new);
        initializers.put(LearnWordHandler.class, this::learnWordHandler);
        initializers.put(QuestionHandler.class, this::questionHandler);
    }

    public <T> void addDefinition(Class<T> serviceId, T service) {
        if (resolved) {
            throw new RuntimeException("Adding definitions into locator, that has already resolved some refs, may lead to unexpected results");
        }

        initializers.put(serviceId, new SharedProvider<>(() -> service));
    }

    public <T> T locate(Class<T> serviceId) {
        resolved = true;
        Supplier<?> initializer = initializers.get(serviceId);
        if (initializer == null) {
            throw new RuntimeException("Unknown service requested: " + serviceId.getName());
        }

        return (T) initializer.get();
    }

    private Bot bot() {
        Bot declaration = new Bot();
        declaration.addHandler(locate(TranslateWordHandler.class));
        declaration.addHandler(locate(LearnWordHandler.class));
        declaration.addHandler(locate(QuestionHandler.class));

        return declaration;
    }

    private LearnWordHandler learnWordHandler() {
        return new LearnWordHandler(locate(DictionaryRepository.class), locate(ProgressRepository.class));
    }

    private QuestionHandler questionHandler() {
        return new QuestionHandler(locate(QuestionRepository.class));
    }

    private DictionaryRepository dictionaryRepository() {
        return new DictionaryRepository(locate(Connection.class));
    }

    private ProgressRepository progressRepository() {
        return new ProgressRepository(locate(Connection.class));
    }

    private QuestionRepository questionRepository() {
        return new QuestionRepository(locate(Connection.class));
    }

    private Telegram telegramBot() {
        String botToken = System.getenv("TG_BOT_TOKEN");

        return new Telegram(botToken, locate(Bot.class));
    }

    private Connection connection() {
        String url = System.getenv("DATABASE_URL");
        String username = System.getenv("DATABASE_USERNAME");
        String password = System.getenv("DATABASE_PASSWORD");

        return new Connection(url, username, password);
    }
}
