package me.nasukhov.TukitaLearner.bot.task;

import me.nasukhov.TukitaLearner.bot.bridge.IOResolver;
import me.nasukhov.TukitaLearner.bot.io.Channel;
import me.nasukhov.TukitaLearner.study.Group;
import me.nasukhov.TukitaLearner.study.ProgressTracker;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ShareFact implements TaskRunner {
    private final ProgressTracker progressTracker;

    private final IOResolver ioResolver;

    public ShareFact(ProgressTracker progressTracker, IOResolver ioResolver) {
        this.progressTracker = progressTracker;
        this.ioResolver = ioResolver;
    }

    @Override
    public String subscribesFor() {
        return "share_fact";
    }

    @Override
    public void runTask(Task task) {
        Optional<String> fact = progressTracker.nextRandomFact(new Group(task.getChannel().id));
        if (fact.isEmpty()) {
            return;
        }
        shareFact(fact.get(), task.getChannel());
    }

    private void shareFact(String fact, Channel with) {
        ioResolver.resolveFor(with).write(fact);
    }
}
