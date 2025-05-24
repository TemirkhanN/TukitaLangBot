package me.nasukhov.tukitalearner.bot.command

import me.nasukhov.tukitalearner.bot.io.Input
import me.nasukhov.tukitalearner.bot.io.Output
import me.nasukhov.tukitalearner.study.ProgressTracker
import org.springframework.stereotype.Component

@Component("askQuestionCommand")
class AskQuestion(
    private val progressTracker: ProgressTracker,
) : Handler {
    companion object {
        private const val NO_MORE_QUESTIONS_LEFT = "У нас пока нет новых вопросов. Проверьте позже"
    }

    override fun supports(input: Input): Boolean = input.isDirectCommand("ask")

    override fun handle(
        input: Input,
        output: Output,
    ) {
        val channel = input.channel
        val result = progressTracker.createRandomForChannel(channel)

        if (result.isEmpty) {
            // TODO share summary. reset progress
            output.write(NO_MORE_QUESTIONS_LEFT)

            return
        }

        val newQuestion = result.get()

        val replies = HashMap<String, String>()
        var optionNum = 0
        for (replyVariant in newQuestion.listVariants()) {
            replies.put(replyVariant, "qh answer ${newQuestion.id} ${++optionNum}")
        }

        output.promptChoice(newQuestion.text, replies)
    }
}
