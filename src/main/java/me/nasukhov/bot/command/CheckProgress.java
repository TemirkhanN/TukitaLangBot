package me.nasukhov.bot.command;

import me.nasukhov.bot.Input;
import me.nasukhov.bot.Output;
import me.nasukhov.study.ChannelStats;
import me.nasukhov.study.ProgressRepository;
import me.nasukhov.study.UserStats;

public class CheckProgress implements Handler {
    private final ProgressRepository progressRepository;

    public CheckProgress(ProgressRepository progressRepository) {
        this.progressRepository = progressRepository;
    }

    @Override
    public void handle(Input input, Output output) {
        ChannelStats cs = this.progressRepository.getChannelStats(input.channel());

        StringBuilder sb = new StringBuilder();
        sb.append("Name | ✅ | ❌\n");
        sb.append("----\n");
        for (UserStats userStats: cs.usersStats()) {
            sb.append(String.format("%s | %-5s | %-5s", output.mention(userStats.userId()), userStats.correctAnswers(), userStats.incorrectAnswers()));
            sb.append("\n");
        }

        output.write(sb.toString());
    }

    @Override
    public boolean supports(Input input) {
        return input.isDirectCommand("stats");
    }
}
