package me.nasukhov.bot.command;

import me.nasukhov.bot.Channel;
import me.nasukhov.bot.Input;
import me.nasukhov.bot.Output;
import me.nasukhov.study.ChannelQuestion;
import me.nasukhov.study.QuestionRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class QuestionHandler implements Handler {
    private static final String Id = "qh";
    private static final String ANSWER_CORRECT = "%s отвечает правильно на вопрос «%s»";
    private static final String ANSWER_INCORRECT = "%s допускает ошибку при ответе на вопрос «%s»";
    private static final String ANSWER_CORRECT_DM = "Правильно «%s».\n • ᴗ •";
    private static final String ANSWER_INCORRECT_DM = "Правильно «%s».\n • ᴖ •";
    private static final String NO_MORE_QUESTIONS_LEFT = "У нас пока нет новых вопросов. Проверьте позже";
    private final QuestionRepository questionRepository;
    public QuestionHandler(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public boolean supports(Input command) {
        return command.isDirectCommand("ask") ||  command.input().startsWith(Id + " answer ");
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
        Optional<ChannelQuestion> result = questionRepository.createRandomForChannel(channel.id);

        if (result.isEmpty()) {
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

        boolean alreadyAnswered = questionRepository.hasReplyInChannel(userId, channel.id, channelQuestionId);
        if (alreadyAnswered) {
            return;
        }

        Optional<ChannelQuestion> result = questionRepository.findQuestionInChannel(channelQuestionId);;
        if (result.isEmpty()) {
            return;
        }

        ChannelQuestion question = result.get();

        int selectedOption = Integer.parseInt(parts[3]);
        boolean isCorrectAnswer = question.isCorrectAnswer(selectedOption);
        questionRepository.addUserAnswer(
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
