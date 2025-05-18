package me.nasukhov.TukitaLearner.bot.command;

import me.nasukhov.TukitaLearner.bot.io.Channel;
import me.nasukhov.TukitaLearner.bot.io.Input;
import me.nasukhov.TukitaLearner.bot.io.Output;
import me.nasukhov.TukitaLearner.study.GroupQuestion;
import me.nasukhov.TukitaLearner.study.GroupQuestionRepository;
import me.nasukhov.TukitaLearner.study.ProgressTracker;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AnswerQuestion implements Handler {
    private static final String ANSWER_CORRECT = "%s, да, правильный ответ «<spoiler>%s</spoiler>».";
    private static final String ANSWER_INCORRECT = "%s, увы, правильный ответ «<spoiler>%s</spoiler>».";
    private final ProgressTracker progressTracker;
    private final AskQuestion askQuestion;

    private final GroupQuestionRepository groupQuestionRepository;

    public AnswerQuestion(
            ProgressTracker progressTracker,
            AskQuestion askQuestion,
            GroupQuestionRepository groupQuestionRepository
    ) {
        this.progressTracker = progressTracker;
        this.askQuestion = askQuestion;
        this.groupQuestionRepository = groupQuestionRepository;
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

        Optional<GroupQuestion> result = progressTracker.findGroupQuestionById(channelQuestionId);
        if (result.isEmpty()) {
            return;
        }

        GroupQuestion question = result.get();

        if (question.hasAnswerFromUser(userId)) {
            return;
        }

        int selectedOption = Integer.parseInt(parts[3]);
        var answer = question.addAnswer(userId, selectedOption);
        groupQuestionRepository.save(question);

        String template = answer.isCorrect ? ANSWER_CORRECT: ANSWER_INCORRECT;
        output.write(String.format(template, input.sender().name(), question.viewAnswer()));

        if (!channel.isPublic()) {
            this.askQuestion.handle(input, output);
        }
    }
}
