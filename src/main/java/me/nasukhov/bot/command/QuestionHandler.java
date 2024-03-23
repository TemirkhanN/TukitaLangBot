package me.nasukhov.bot.command;

import me.nasukhov.bot.Channel;
import me.nasukhov.bot.Command;
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
    private static final String NO_MORE_QUESTIONS_LEFT = "У нас пока нет новых вопросов. Проверьте позже";
    private final QuestionRepository questionRepository;
    public QuestionHandler(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public boolean supports(Command command) {
        return command.isDirectCommand("ask") ||  command.input().startsWith(Id + " answer ");
    }

    @Override
    public void handle(Command command) {
        if (command.isDirectCommand("ask")) {
            handleAsk(command);

            return;
        }

        if (command.input().startsWith(QuestionHandler.Id + " answer ")) {
            handleAnswer(command);

            return;
        }
    }

    private void handleAsk(Command command) {
        Channel channel = command.channel();
        Optional<ChannelQuestion> result = questionRepository.createRandomForChannel(channel.id);

        if (result.isEmpty()) {
            channel.sendMessage(NO_MORE_QUESTIONS_LEFT);

            return;
        }

        ChannelQuestion newQuestion = result.get();

        Map<String, String> replies = new HashMap<>();
        int optionNum = 0;
        for (String replyVariant : newQuestion.listVariants()) {
            replies.put(replyVariant, String.format("%s answer %s %d", QuestionHandler.Id, newQuestion.getId().toString(), ++optionNum));
        }

        channel.sendQuestion(newQuestion.getText(), replies);
    }

    private void handleAnswer(Command command) {
        String[] parts = command.input().split("\\s+", 4);
        if (parts.length < 4) {
            return;
        }

        UUID channelQuestionId;
        try {
            channelQuestionId = UUID.fromString(parts[2]);
        } catch (IllegalArgumentException e) {
            return;
        }

        String channelId = command.channel().id;
        String userId = command.sender().id();

        boolean alreadyAnswered = questionRepository.hasReplyInChannel(userId, channelId, channelQuestionId);
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
                channelId,
                isCorrectAnswer
        );

        String msg = isCorrectAnswer ? ANSWER_CORRECT: ANSWER_INCORRECT;

        command.channel().sendMessage(String.format(msg, command.sender().name(), question.getText()));
    }
}
