package me.nasukhov.tukitalearner.bot

import me.nasukhov.bot.command.Handler
import me.nasukhov.bot.io.Input
import me.nasukhov.bot.io.NoOutput
import me.nasukhov.bot.io.Output
import me.nasukhov.bot.io.TextOutput
import me.nasukhov.tukitalearner.study.GroupQuestionRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

private data class AnswerData(
    val questionId: UUID,
    val selectedOption: Int,
)

@Component
class AnswerQuestion(
    private val groupQuestionRepository: GroupQuestionRepository,
) : Handler {
    companion object {
        private const val ANSWER_CORRECT = "%s, да, правильный ответ «<spoiler>%s</spoiler>»."
        private const val ANSWER_INCORRECT = "%s, увы, правильный ответ «<spoiler>%s</spoiler>»."
    }

    override fun supports(input: Input): Boolean = input.raw.startsWith("qh answer ")

    @Transactional
    override fun handle(input: Input): Output {
        if (!supports(input)) {
            return NoOutput()
        }

        val answerData = parseGroupQuestionId(input.raw)
        if (answerData == null) {
            return NoOutput()
        }

        val result = groupQuestionRepository.findByIdWithAnswers(answerData.questionId)
        if (result.isEmpty) {
            return NoOutput()
        }

        val userId = input.sender.id
        val question = result.get()
        if (question.hasAnswerFromUser(userId)) {
            return NoOutput()
        }

        val answer = question.addAnswer(userId, answerData.selectedOption)

        val template: String = if (answer.isCorrect) ANSWER_CORRECT else ANSWER_INCORRECT

        return TextOutput(
            String.format(
                template,
                input.sender.name,
                question.viewAnswer(),
            ),
        )
    }

    private fun parseGroupQuestionId(input: String): AnswerData? {
        val parts = input.split("\\s+".toRegex(), limit = 4).toTypedArray()
        if (parts.size < 4) {
            return null
        }

        return try {
            AnswerData(UUID.fromString(parts[2]), parts[3].toInt())
        } catch (_: IllegalArgumentException) {
            null
        }
    }
}
