package me.nasukhov.TukitaLearner.bot.task;

import me.nasukhov.TukitaLearner.bot.bridge.IOResolver;
import me.nasukhov.TukitaLearner.bot.io.Channel;
import me.nasukhov.TukitaLearner.study.Group;
import me.nasukhov.TukitaLearner.study.ProgressRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ShareFact implements TaskRunner {
    private final ProgressRepository progressRepository;

    private final IOResolver ioResolver;

    public ShareFact(ProgressRepository progressRepository, IOResolver ioResolver) {
        this.progressRepository = progressRepository;
        this.ioResolver = ioResolver;
    }

    @Override
    public String subscribesFor() {
        return "share_fact";
    }

    @Override
    public void runTask(Task task) {
        Optional<String> fact = progressRepository.nextRandomFact(new Group(task.channel().id));
        if (fact.isEmpty()) {
            return;
        }
        shareFact(fact.get(), task.channel());
    }

    private void shareFact(String fact, Channel with) {
        ioResolver.resolveFor(with).write(fact);
    }
}
