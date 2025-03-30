package me.nasukhov.TukitaLearner.bot.command;

import me.nasukhov.TukitaLearner.bot.io.Input;
import me.nasukhov.TukitaLearner.bot.io.Output;
import me.nasukhov.TukitaLearner.study.Group;
import me.nasukhov.TukitaLearner.study.GroupStats;
import me.nasukhov.TukitaLearner.study.ProgressRepository;
import me.nasukhov.TukitaLearner.study.StudentStats;
import org.springframework.stereotype.Component;

@Component
public class CheckProgress implements Handler {
    private final ProgressRepository progressRepository;

    public CheckProgress(ProgressRepository progressRepository) {
        this.progressRepository = progressRepository;
    }

    @Override
    public void handle(Input input, Output output) {
        Group group = new Group(input.channel().id);
        GroupStats cs = this.progressRepository.getGroupStats(group);

        StringBuilder sb = new StringBuilder();
        sb.append("Name | ✅ | ❌\n");
        sb.append("----\n");
        for (StudentStats studentStats : cs.usersStats()) {
            sb.append(String.format("%s | %-5s | %-5s", output.mention(studentStats.studentId()), studentStats.correctAnswers(), studentStats.incorrectAnswers()));
            sb.append("\n");
        }

        output.write(sb.toString());
    }

    @Override
    public boolean supports(Input input) {
        return input.isDirectCommand("stats");
    }
}
