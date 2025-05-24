package me.nasukhov.TukitaLearner.bot.command

import me.nasukhov.TukitaLearner.bot.io.Input
import me.nasukhov.TukitaLearner.bot.io.Output
import me.nasukhov.TukitaLearner.study.Group
import me.nasukhov.TukitaLearner.study.ProgressTracker
import org.springframework.stereotype.Component

@Component
class CheckProgress(private val progressTracker: ProgressTracker) : Handler {
    override fun handle(input: Input, output: Output) {
        val group = Group(input.channel.id)
        val cs = progressTracker.getGroupStats(group)

        val sb = StringBuilder()
        sb.append("Name | ✅ | ❌\n")
        sb.append("----\n")
        for (studentStats in cs.usersStats) {
            sb.append(
                String.format(
                    "%s | %-5s | %-5s",
                    output.mention(studentStats.studentId),
                    studentStats.correctAnswers,
                    studentStats.incorrectAnswers
                )
            )
            sb.append("\n")
        }

        output.write(sb.toString())
    }

    override fun supports(input: Input): Boolean {
        return input.isDirectCommand("stats")
    }
}
