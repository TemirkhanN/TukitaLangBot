package me.nasukhov.tukitalearner.bot.command

import me.nasukhov.tukitalearner.bot.io.Input
import me.nasukhov.tukitalearner.bot.io.Output
import me.nasukhov.tukitalearner.study.GroupRepository
import me.nasukhov.tukitalearner.study.ProgressTracker
import org.springframework.stereotype.Component
import java.util.Locale

@Component
class CheckProgress(
    private val progressTracker: ProgressTracker,
    private val groupRepository: GroupRepository,
) : Handler {
    override fun handle(
        input: Input,
        output: Output,
    ) {
        val group = groupRepository.findById(input.channel.id).get()

        val groupStats = progressTracker.getGroupStats(group)

        val sb = StringBuilder()
        sb.append("Name | ✅ | ❌\n")
        sb.append("----\n")
        for (studentStats in groupStats.usersStats) {
            sb.append(
                String.format(
                    Locale.UK,
                    "%s | %-5s | %-5s",
                    output.mention(studentStats.studentId),
                    studentStats.correctAnswers,
                    studentStats.incorrectAnswers,
                ),
            )
            sb.append("\n")
        }

        output.write(sb.toString())
    }

    override fun supports(input: Input): Boolean = input.isDirectCommand("stats")
}
