package me.nasukhov.TukitaLearner.bot.task;

import me.nasukhov.TukitaLearner.bot.bridge.IOResolver;
import me.nasukhov.TukitaLearner.bot.io.Channel;
import me.nasukhov.TukitaLearner.bot.io.Output;
import me.nasukhov.TukitaLearner.study.GroupQuestion;
import me.nasukhov.TukitaLearner.study.ProgressRepository;
import me.nasukhov.TukitaLearner.study.Time;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component("askQuestionTask")
public class AskQuestion implements TaskRunner {
    private final ProgressRepository progressRepository;
    private final IOResolver ioResolver;

    public AskQuestion(ProgressRepository progressRepository, IOResolver ioResolver) {
        this.progressRepository = progressRepository;
        this.ioResolver = ioResolver;
    }

    @Override
    public String subscribesFor() {
        return "ask_question";
    }

    @Override
    public void runTask(Task task) {
        if (!subscribesFor().equals(task.name())) {
            throw new RuntimeException("Runner does not know how to execute the given task");
        }

        // We don't want it to work at night
        if (Time.isOffHours()) {
            return;
        }

        ask(task.channel());
    }

    public void ask(Channel channel) {
        Optional<GroupQuestion> result = progressRepository.createRandomForChannel(channel.id);
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
