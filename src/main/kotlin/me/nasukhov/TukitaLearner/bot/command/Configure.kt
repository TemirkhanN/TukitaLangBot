package me.nasukhov.TukitaLearner.bot.command

import jakarta.persistence.EntityManager
import me.nasukhov.TukitaLearner.bot.io.Input
import me.nasukhov.TukitaLearner.bot.io.Output
import me.nasukhov.TukitaLearner.study.Group
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.regex.Matcher
import java.util.regex.Pattern

@Component
class Configure(private val db: EntityManager) : Handler {
    companion object {
        private val INTERVAL_PATTERN: Pattern = Pattern.compile("^cfg .+ (\\d+)([mhd])$")
        private const val MIN_INTERVAL = 60
        private const val MAX_INTERVAL = 7 * 24 * 60
    }

    override fun supports(input: Input): Boolean {
        return input.isDirectCommand("config") || input.raw.startsWith("cfg ")
    }

    @Transactional
    override fun handle(input: Input, output: Output) {
        if (!supports(input)) {
            return
        }

        if (input.isDirectCommand("config")) {
            output.promptChoice(
                "Настройка", mapOf(
                    "Включить ежедневные факты" to "cfg facts enable",
                    "Выключить ежедневные факты" to "cfg facts disable",
                    "Включить авто-вопросы" to "cfg asker enable",
                    "Выключить авто-вопросы" to "cfg asker disable",
                    "Спрашивать каждый час" to "cfg asker interval 1h",
                    "Спрашивать каждые 2 часа" to "cfg asker interval 2h",
                    "Спрашивать раз в день" to "cfg asker interval 1d",
                )
            )

            return
        }

        val group = Group(input.channel.id)
        val preferences = group.preferences(db)

        when (true) {
            (input.raw == "cfg asker enable") -> {
                preferences.enableAutoAsker(true)
                output.write("Авто-вопросы включены")
            }

            (input.raw == ("cfg asker disable")) -> {
                preferences.enableAutoAsker(false)
                output.write("Авто-вопросы выключены")
            }

            (input.raw == ("cfg facts enable")) -> {
                preferences.enableFactSharing(true)
                output.write("Факты включены")
            }

            (input.raw == ("cfg facts disable")) -> {
                preferences.enableFactSharing(false)
                output.write("Факты выключены")
            }

            (input.raw.startsWith(("cfg asker interval"))) -> {
                preferences.autoAskEveryXMinutes(parseInterval(input))
                output.write("Интервал между авто-вопросами сохранен")
            }

            (input.raw.startsWith(("cfg fact interval"))) -> {
                preferences.shareFactEveryXMinutes(parseInterval(input))
                output.write("Интервал между фактами сохранен")
            }

            else -> output.write("Unknown configuration command!")
        }
    }

    private fun parseInterval(input: Input): Int {
        val matcher: Matcher = INTERVAL_PATTERN.matcher(input.toString())
        if (!matcher.find()) {
            return 0
        }

        var interval = matcher.group(1).toInt()
        when (matcher.group(2)) {
            "d" -> interval *= 24 * 60
            "h" -> interval *= 60
            else -> {}
        }

        if (interval > MAX_INTERVAL) {
            interval = MAX_INTERVAL
        }

        if (interval < MIN_INTERVAL) {
            interval = MIN_INTERVAL
        }

        return interval
    }
}
