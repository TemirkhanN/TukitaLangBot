package me.nasukhov.tukitalearner.bot

import me.nasukhov.bot.command.Handler
import me.nasukhov.bot.io.Input
import me.nasukhov.bot.io.NoOutput
import me.nasukhov.bot.io.Output
import me.nasukhov.bot.io.TextOutput
import me.nasukhov.tukitalearner.study.GroupRepository
import me.nasukhov.tukitalearner.study.ProgressTracker
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.Locale

@Component
class CheckProgress(
    private val progressTracker: ProgressTracker,
    private val groupRepository: GroupRepository,
) : Handler {
    @Transactional
    override fun handle(input: Input): Output {
        if (!supports(input)) {
            return NoOutput()
        }

        val group = groupRepository.findById(input.channel.id)
        if (group == null || !group.isActive) {
            return NoOutput()
        }

        val groupStats = progressTracker.getGroupStats(group)

        val sb = StringBuilder()
        sb.append("Name | ✅ | ❌\n")
        sb.append("----\n")
        for (studentStats in groupStats.usersStats) {
            sb.append(
                String.format(
                    Locale.UK,
                    "%s | %-5s | %-5s",
                    Output.Companion.mention(studentStats.studentId),
                    studentStats.correctAnswers,
                    studentStats.incorrectAnswers,
                ),
            )
            sb.append("\n")
        }

        return TextOutput(sb.toString())
    }

    override fun supports(input: Input): Boolean = input.isDirectCommand("stats")
}
