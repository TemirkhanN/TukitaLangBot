package me.nasukhov.TukitaLearner.bot.command;

import me.nasukhov.TukitaLearner.bot.io.Channel;
import me.nasukhov.TukitaLearner.bot.io.Input;
import me.nasukhov.TukitaLearner.bot.io.Output;
import me.nasukhov.TukitaLearner.study.GroupQuestion;
import me.nasukhov.TukitaLearner.study.ProgressRepository;

import java.util.*;

public class AnswerQuestion implements Handler {
    private static final String ANSWER_CORRECT = "%s, да, правильный ответ «<spoiler>%s</spoiler>».";
    private static final String ANSWER_INCORRECT = "%s, увы, правильный ответ «<spoiler>%s</spoiler>».";
    private final ProgressRepository progressRepository;
    private final AskQuestion askQuestion;

    public AnswerQuestion(ProgressRepository progressRepository, AskQuestion askQuestion) {
        this.progressRepository = progressRepository;
        this.askQuestion = askQuestion;
    }

    @Override
    public boolean supports(Input input) {
        return input.input().startsWith("qh answer ");
    }

    @Override
    public void handle(Input input, Output output) {
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

        Optional<GroupQuestion> result = progressRepository.findQuestionInChannel(channelQuestionId);
        if (result.isEmpty()) {
            return;
        }

        GroupQuestion question = result.get();

        int selectedOption = Integer.parseInt(parts[3]);
        boolean isCorrectAnswer = question.isCorrectAnswer(selectedOption);
        progressRepository.addUserAnswer(
                channelQuestionId,
                userId,
                channel.id,
                isCorrectAnswer
        );

        String template = isCorrectAnswer ? ANSWER_CORRECT: ANSWER_INCORRECT;
        output.write(String.format(template, input.sender().name(), question.viewAnswer()));

        if (!channel.isPublic()) {
            this.askQuestion.handle(input, output);
        }
    }
}
