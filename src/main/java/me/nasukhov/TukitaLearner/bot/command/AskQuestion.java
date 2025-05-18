package me.nasukhov.TukitaLearner.bot.command;

import me.nasukhov.TukitaLearner.bot.io.Input;
import me.nasukhov.TukitaLearner.bot.io.Output;
import me.nasukhov.TukitaLearner.study.GroupQuestion;
import me.nasukhov.TukitaLearner.study.ProgressTracker;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component("askQuestionCommand")
public class AskQuestion implements Handler {
    private static final String NO_MORE_QUESTIONS_LEFT = "У нас пока нет новых вопросов. Проверьте позже";
    private final ProgressTracker progressTracker;

    public AskQuestion(ProgressTracker progressTracker) {
        this.progressTracker = progressTracker;
    }

    @Override
    public boolean supports(Input input) {
        return input.isDirectCommand("ask");
    }

    @Override
    public void handle(Input input, Output output) {
        var channel = input.channel();
        Optional<GroupQuestion> result = progressTracker.createRandomForChannel(channel);

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
