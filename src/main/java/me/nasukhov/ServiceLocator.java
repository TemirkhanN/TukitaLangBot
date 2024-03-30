package me.nasukhov;

import me.nasukhov.bot.Bot;
import me.nasukhov.bot.ChannelRepository;
import me.nasukhov.bot.bridge.tg.Telegram;
import me.nasukhov.bot.command.LearnInterestingHandler;
import me.nasukhov.bot.command.LearnWordHandler;
import me.nasukhov.bot.command.QuestionHandler;
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

public final class ServiceLocator {
    public static ServiceLocator instance;

    private final Map<Class<?>, Supplier<?>> initializers = new HashMap<>();

    private boolean resolved = false;

    public ServiceLocator() {
        initializers.put(Connection.class, new SharedProvider<>(this::connection));
        initializers.put(DictionaryRepository.class, new SharedProvider<>(this::dictionaryRepository));
        initializers.put(ProgressRepository.class, new SharedProvider<>(this::progressRepository));
        initializers.put(QuestionRepository.class, new SharedProvider<>(this::questionRepository));
        initializers.put(ChannelRepository.class, new SharedProvider<>(this::channelRepository));
        initializers.put(Telegram.class, new SharedProvider<>(this::telegramBot));
        initializers.put(Bot.class, new SharedProvider<>(this::bot));
        initializers.put(LearnWordHandler.class, new SharedProvider<>(this::learnWordHandler));
        initializers.put(LearnInterestingHandler.class, new SharedProvider<>(this::learnInterestingHandler));
        initializers.put(QuestionHandler.class, this::questionHandler);

        // TODO looks weird
        if (instance == null) {
            instance = this;
        }
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
        Bot declaration = new Bot(locate(ChannelRepository.class));
        declaration.addHandler(locate(LearnWordHandler.class));
        declaration.addHandler(locate(LearnInterestingHandler.class));
        declaration.addHandler(locate(QuestionHandler.class));

        return declaration;
    }

    private LearnWordHandler learnWordHandler() {
        return new LearnWordHandler(locate(DictionaryRepository.class), locate(ProgressRepository.class));
    }

    private LearnInterestingHandler learnInterestingHandler() {
        return new LearnInterestingHandler(locate(ChannelRepository.class));
    }

    private QuestionHandler questionHandler() {
        return new QuestionHandler(locate(ProgressRepository.class), locate(ChannelRepository.class));
    }

    private DictionaryRepository dictionaryRepository() {
        return new DictionaryRepository(locate(Connection.class));
    }

    private ProgressRepository progressRepository() {
        return new ProgressRepository(locate(Connection.class), locate(QuestionRepository.class));
    }

    private QuestionRepository questionRepository() {
        return new QuestionRepository(locate(Connection.class));
    }

    private ChannelRepository channelRepository() {
        return new ChannelRepository(locate(Connection.class));
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
