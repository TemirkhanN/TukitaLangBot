package me.nasukhov.bot.command;

import me.nasukhov.bot.io.Input;
import me.nasukhov.bot.io.Output;
import me.nasukhov.study.Group;
import me.nasukhov.study.GroupStats;
import me.nasukhov.study.ProgressRepository;
import me.nasukhov.study.StudentStats;

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
