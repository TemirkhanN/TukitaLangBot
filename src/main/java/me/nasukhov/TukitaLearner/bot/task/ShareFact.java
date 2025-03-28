package me.nasukhov.TukitaLearner.bot.task;

import me.nasukhov.TukitaLearner.bot.bridge.IOResolver;
import me.nasukhov.TukitaLearner.bot.io.Channel;
import me.nasukhov.TukitaLearner.study.Group;
import me.nasukhov.TukitaLearner.study.ProgressRepository;

import java.util.Optional;

public class ShareFact implements TaskRunner {
    private final ProgressRepository progressRepository;

    public ShareFact(ProgressRepository progressRepository) {
        this.progressRepository = progressRepository;
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
        IOResolver.resolveFor(with).write(fact);
    }
}
