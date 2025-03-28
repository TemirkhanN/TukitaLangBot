package me.nasukhov.TukitaLearner.bot.command;

import me.nasukhov.TukitaLearner.bot.io.Channel;
import me.nasukhov.TukitaLearner.bot.io.Input;
import me.nasukhov.TukitaLearner.bot.io.Output;
import me.nasukhov.TukitaLearner.study.GroupQuestion;
import me.nasukhov.TukitaLearner.study.ProgressRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AskQuestion implements Handler {
    private static final String NO_MORE_QUESTIONS_LEFT = "У нас пока нет новых вопросов. Проверьте позже";
    private final ProgressRepository progressRepository;

    public AskQuestion(ProgressRepository progressRepository) {
        this.progressRepository = progressRepository;
    }

    @Override
    public boolean supports(Input input) {
        return input.isDirectCommand("ask");
    }

    @Override
    public void handle(Input input, Output output) {
        ask(input.channel(), output);
    }

    private void ask(Channel channel, Output output) {
        Optional<GroupQuestion> result = progressRepository.createRandomForChannel(channel.id);

        if (result.isEmpty()) {
            // TODO share summary. reset progress
            output.write(NO_MORE_QUESTIONS_LEFT);

            return;
        }

        GroupQuestion newQuestion = result.get();

        Map<String, String> replies = new HashMap<>();
        int optionNum = 0;
        for (String replyVariant : newQuestion.listVariants()) {
            replies.put(replyVariant, String.format("qh answer %s %d", newQuestion.getId().toString(), ++optionNum));
        }

        output.promptChoice(newQuestion.getText(), replies);
    }
}
