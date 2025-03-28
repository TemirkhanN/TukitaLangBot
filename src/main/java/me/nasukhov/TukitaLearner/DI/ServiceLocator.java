package me.nasukhov.TukitaLearner.DI;

import me.nasukhov.TukitaLearner.bot.Bot;
import me.nasukhov.TukitaLearner.bot.io.ChannelRepository;
import me.nasukhov.TukitaLearner.bot.bridge.tg.Telegram;
import me.nasukhov.TukitaLearner.bot.command.*;
import me.nasukhov.TukitaLearner.bot.task.ShareFact;
import me.nasukhov.TukitaLearner.bot.task.TaskManager;
import me.nasukhov.TukitaLearner.db.Connection;
import me.nasukhov.TukitaLearner.dictionary.DictionaryRepository;
import me.nasukhov.TukitaLearner.study.ProgressRepository;
import me.nasukhov.TukitaLearner.study.QuestionRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public final class ServiceLocator {
    private static ServiceLocator instance;

    private final Map<Class<?>, Supplier<?>> initializers;

    private boolean resolved = false;

    public static void resetInstance() {
        instance = null;
    }

    public static ServiceLocator getInstance() {
        // TODO looks weird
        if (instance == null) {
            instance = new ServiceLocator();
        }

        return instance;
    }

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
        initializers.put(me.nasukhov.TukitaLearner.bot.task.AskQuestion.class, new SharedProvider<>(this::askQuestionTask));
        initializers.put(ShareFact.class, new SharedProvider<>(this::shareFact));
        initializers.put(Configure.class, new SharedProvider<>(this::configurator));
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

    private me.nasukhov.TukitaLearner.bot.task.AskQuestion askQuestionTask() {
        return new me.nasukhov.TukitaLearner.bot.task.AskQuestion(locate(ProgressRepository.class));
    }

    private ShareFact shareFact() {
        return new ShareFact(locate(ProgressRepository.class));
    }

    private TaskManager taskManager() {
        TaskManager taskManager = new TaskManager(locate(Connection.class));
        taskManager.registerRunner(locate(ShareFact.class));
        taskManager.registerRunner(locate(me.nasukhov.TukitaLearner.bot.task.AskQuestion.class));

        return taskManager;
    }

    private Configure configurator() {
        return new Configure();
    }
}
