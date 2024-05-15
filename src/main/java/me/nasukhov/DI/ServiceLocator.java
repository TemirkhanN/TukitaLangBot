package me.nasukhov.DI;

import me.nasukhov.bot.Bot;
import me.nasukhov.bot.io.ChannelRepository;
import me.nasukhov.bot.bridge.tg.Telegram;
import me.nasukhov.bot.command.*;
import me.nasukhov.bot.task.ShareFact;
import me.nasukhov.bot.task.TaskManager;
import me.nasukhov.db.Connection;
import me.nasukhov.dictionary.DictionaryRepository;
import me.nasukhov.study.ProgressRepository;
import me.nasukhov.study.QuestionRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public final class ServiceLocator {
    public static ServiceLocator instance;

    private final Map<Class<?>, Supplier<?>> initializers;

    private boolean resolved = false;

    public ServiceLocator() {
        initializers = new ConcurrentHashMap<>();

        initializers.put(Connection.class, new SharedProvider<>(this::connection));
        initializers.put(DictionaryRepository.class, new SharedProvider<>(this::dictionaryRepository));
        initializers.put(ProgressRepository.class, new SharedProvider<>(this::progressRepository));
        initializers.put(QuestionRepository.class, new SharedProvider<>(this::questionRepository));
        initializers.put(ChannelRepository.class, new SharedProvider<>(this::channelRepository));
        initializers.put(Telegram.class, new SharedProvider<>(this::telegramBot));
        initializers.put(Bot.class, new SharedProvider<>(this::bot));
        initializers.put(LearnWord.class, new SharedProvider<>(this::learnWordHandler));
        initializers.put(AskQuestion.class, new SharedProvider<>(this::askQuestionHandler));
        initializers.put(AnswerQuestion.class, new SharedProvider<>(this::answerQuestionHandler));
        initializers.put(CheckProgress.class, new SharedProvider<>(this::checkProgressHandler));
        initializers.put(TaskManager.class, new SharedProvider<>(this::taskManager));
        initializers.put(me.nasukhov.bot.task.AskQuestion.class, new SharedProvider<>(this::askQuestionTask));
        initializers.put(ShareFact.class, new SharedProvider<>(this::shareFact));
        initializers.put(Configure.class, new SharedProvider<>(this::configurator));

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
        Bot declaration = new Bot(locate(ChannelRepository.class), locate(TaskManager.class));
        declaration.addHandler(locate(LearnWord.class));
        declaration.addHandler(locate(AnswerQuestion.class));
        declaration.addHandler(locate(AskQuestion.class));
        declaration.addHandler(locate(CheckProgress.class));
        declaration.addHandler(locate(Configure.class));

        return declaration;
    }

    private LearnWord learnWordHandler() {
        return new LearnWord(locate(DictionaryRepository.class), locate(ProgressRepository.class));
    }

    private AnswerQuestion answerQuestionHandler() {
        return new AnswerQuestion(locate(ProgressRepository.class), locate(AskQuestion.class));
    }

    private AskQuestion askQuestionHandler() {
        return new AskQuestion(locate(ProgressRepository.class));
    }

    private CheckProgress checkProgressHandler() {
        return new CheckProgress(locate(ProgressRepository.class));
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

    private me.nasukhov.bot.task.AskQuestion askQuestionTask() {
        return new me.nasukhov.bot.task.AskQuestion(locate(ProgressRepository.class));
    }

    private ShareFact shareFact() {
        return new ShareFact(locate(ProgressRepository.class));
    }

    private TaskManager taskManager() {
        TaskManager taskManager = new TaskManager(locate(Connection.class));
        taskManager.registerRunner(locate(ShareFact.class));
        taskManager.registerRunner(locate(me.nasukhov.bot.task.AskQuestion.class));

        return taskManager;
    }

    private Configure configurator() {
        return new Configure();
    }
}
