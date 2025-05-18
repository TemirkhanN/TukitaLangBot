package me.nasukhov.TukitaLearner.bot.task;

import me.nasukhov.TukitaLearner.bot.bridge.IOResolver;
import me.nasukhov.TukitaLearner.bot.io.Channel;
import me.nasukhov.TukitaLearner.bot.io.Output;
import me.nasukhov.TukitaLearner.study.GroupQuestion;
import me.nasukhov.TukitaLearner.study.ProgressTracker;
import me.nasukhov.TukitaLearner.study.Time;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component("askQuestionTask")
public class AskQuestion implements TaskRunner {
    private final ProgressTracker progressTracker;
    private final IOResolver ioResolver;

    public AskQuestion(ProgressTracker progressTracker, IOResolver ioResolver) {
        this.progressTracker = progressTracker;
        this.ioResolver = ioResolver;
    }

    @Override
    public String subscribesFor() {
        return "ask_question";
    }

    @Override
    public void runTask(Task task) {
        if (!subscribesFor().equals(task.getName())) {
            throw new RuntimeException("Runner does not know how to execute the given task");
        }

        // We don't want it to work at night
        if (Time.isOffHours()) {
            return;
        }

        ask(task.getChannel());
    }

    public void ask(Channel channel) {
        Optional<GroupQuestion> result = progressTracker.createRandomForChannel(channel);
        if (result.isEmpty()) {
            return;
        }

        GroupQuestion newQuestion = result.get();

        Map<String, String> replies = new HashMap<>();
        int optionNum = 0;
        for (String replyVariant : newQuestion.listVariants()) {
            replies.put(replyVariant, String.format("qh answer %s %d", newQuestion.getId().toString(), ++optionNum));
        }

        Output output = ioResolver.resolveFor(channel);
        output.promptChoice(newQuestion.getText(), replies);
    }
}
