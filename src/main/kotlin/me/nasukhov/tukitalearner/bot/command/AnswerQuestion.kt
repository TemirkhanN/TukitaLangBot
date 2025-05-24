package me.nasukhov.tukitalearner.bot.command

import me.nasukhov.tukitalearner.bot.io.Input
import me.nasukhov.tukitalearner.bot.io.Output
import me.nasukhov.tukitalearner.study.GroupQuestionRepository
import org.springframework.stereotype.Component
import java.util.*

@Component
class AnswerQuestion(
    private val askQuestion: AskQuestion,
    private val groupQuestionRepository: GroupQuestionRepository,
) : Handler {
    companion object {
        private const val ANSWER_CORRECT = "%s, да, правильный ответ «<spoiler>%s</spoiler>»."
        private const val ANSWER_INCORRECT = "%s, увы, правильный ответ «<spoiler>%s</spoiler>»."
    }

    override fun supports(input: Input): Boolean = input.raw.startsWith("qh answer ")

    override fun handle(
        input: Input,
        output: Output,
    ) {
        val parts = input.raw.split("\\s+".toRegex(), limit = 4).toTypedArray()
        if (parts.size < 4) {
            return
        }

        val channelQuestionId: UUID
        try {
            channelQuestionId = UUID.fromString(parts[2])
        } catch (_: IllegalArgumentException) {
            return
        }

        val channel = input.channel
        val userId = input.sender.id

        val result = groupQuestionRepository.findByIdWithAnswers(channelQuestionId)
        if (result.isEmpty) {
            return
        }

        val question = result.get()

        if (question.hasAnswerFromUser(userId)) {
            return
        }

        val selectedOption = parts[3].toInt()
        val answer = question.addAnswer(userId, selectedOption)
        groupQuestionRepository.save(question)

        val template: String = if (answer.isCorrect) ANSWER_CORRECT else ANSWER_INCORRECT
        output.write(String.format(template, input.sender.name, question.viewAnswer()))

        if (!channel.isPublic) {
            this.askQuestion.handle(input, output)
        }
    }
}
