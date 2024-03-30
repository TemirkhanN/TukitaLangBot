package me.nasukhov.bot.command;

import me.nasukhov.bot.*;
import me.nasukhov.bot.bridge.OutputResolver;
import me.nasukhov.study.ChannelQuestion;
import me.nasukhov.study.ProgressRepository;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class QuestionHandler implements Handler {
    private static final String Id = "qh";
    private static final String ANSWER_CORRECT = "%s отвечает правильно на вопрос «%s»";
    private static final String ANSWER_INCORRECT = "%s допускает ошибку при ответе на вопрос «%s»";
    private static final String ANSWER_CORRECT_DM = "Правильно «%s».\n • ᴗ •";
    private static final String ANSWER_INCORRECT_DM = "Правильно «%s».\n • ᴖ •";
    private static final String NO_MORE_QUESTIONS_LEFT = "У нас пока нет новых вопросов. Проверьте позже";
    private final ProgressRepository progressRepository;

    private final ChannelRepository channelRepository;

    public QuestionHandler(
            ProgressRepository progressRepository,
            ChannelRepository channelRepository
    ) {
        this.progressRepository = progressRepository;
        this.channelRepository = channelRepository;

        registerTasks();
    }

    @Override
    public boolean supports(Input command) {
        return command.isDirectCommand("ask") ||  command.input().startsWith(Id + " answer ");
    }

    private void registerTasks() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable autoAskQuestionInGroups = () -> {
            for (Channel channel: channelRepository.list()) {
                handleAsk(
                        new Input("", channel, User.System),
                        OutputResolver.resolveFor(channel)
                );
            }
        };

        scheduler.scheduleAtFixedRate(autoAskQuestionInGroups, 0, 2, TimeUnit.HOURS);
    }

    @Override
    public void handle(Input input, Output output) {
        if (input.isDirectCommand("ask")) {
            handleAsk(input, output);

            return;
        }

        if (input.input().startsWith(QuestionHandler.Id + " answer ")) {
            handleAnswer(input, output);

            return;
        }
    }

    private void handleAsk(Input input, Output output) {
        Channel channel = input.channel();
        Optional<ChannelQuestion> result = progressRepository.createRandomForChannel(channel.id);

        if (result.isEmpty()) {
            // TODO share summary. reset progress
            output.write(NO_MORE_QUESTIONS_LEFT);

            return;
        }

        ChannelQuestion newQuestion = result.get();

        Map<String, String> replies = new HashMap<>();
        int optionNum = 0;
        for (String replyVariant : newQuestion.listVariants()) {
            replies.put(replyVariant, String.format("%s answer %s %d", QuestionHandler.Id, newQuestion.getId().toString(), ++optionNum));
        }

        output.promptChoice(newQuestion.getText(), replies);
    }

    private void handleAnswer(Input input, Output output) {
        String[] parts = input.input().split("\\s+", 4);
        if (parts.length < 4) {
            return;
        }

        UUID channelQuestionId;
        try {
            channelQuestionId = UUID.fromString(parts[2]);
        } catch (IllegalArgumentException e) {
            return;
        }

        Channel channel = input.channel();
        String userId = input.sender().id();

        boolean alreadyAnswered = progressRepository.hasReplyInChannel(userId, channel.id, channelQuestionId);
        if (alreadyAnswered) {
            return;
        }

        Optional<ChannelQuestion> result = progressRepository.findQuestionInChannel(channelQuestionId);;
        if (result.isEmpty()) {
            return;
        }

        ChannelQuestion question = result.get();

        int selectedOption = Integer.parseInt(parts[3]);
        boolean isCorrectAnswer = question.isCorrectAnswer(selectedOption);
        progressRepository.addUserAnswer(
                channelQuestionId,
                userId,
                channel.id,
                isCorrectAnswer
        );

        if (channel.isPublic()) {
            String template = isCorrectAnswer ? ANSWER_CORRECT: ANSWER_INCORRECT;

            output.write(String.format(template, input.sender().name(), question.getText()));

            return;
        }

        String template = isCorrectAnswer ? ANSWER_CORRECT_DM: ANSWER_INCORRECT_DM;
        output.write(String.format(template, question.viewAnswer()));
        handleAsk(input, output);
    }
}
