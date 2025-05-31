package me.nasukhov.tukitalearner.bot

import me.nasukhov.bot.command.Handler
import me.nasukhov.bot.io.Input
import me.nasukhov.bot.io.Output
import me.nasukhov.bot.io.PromptOutput
import me.nasukhov.bot.io.TextOutput
import me.nasukhov.tukitalearner.study.GroupRepository
import me.nasukhov.tukitalearner.study.ProgressTracker
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component("askQuestionCommand")
class AskQuestion(
    private val progressTracker: ProgressTracker,
    private val groupRepository: GroupRepository,
) : Handler {
    companion object {
        private const val NO_MORE_QUESTIONS_LEFT = "У нас пока нет новых вопросов. Проверьте позже"
    }

    override fun supports(input: Input): Boolean = input.isDirectCommand("ask")

    @Transactional
    override fun handle(input: Input): Output {
        val group = groupRepository.findById(input.channel.id)
        val result = progressTracker.createRandomForGroup(group!!)

        if (result.isEmpty) {
            return TextOutput(NO_MORE_QUESTIONS_LEFT)
        }

        val newQuestion = result.get()

        val replies = HashMap<String, String>()
        var optionNum = 0
        for (replyVariant in newQuestion.listVariants()) {
            replies.put(replyVariant, "qh answer ${newQuestion.id} ${++optionNum}")
        }

        return PromptOutput(newQuestion.text, replies)
    }
}
