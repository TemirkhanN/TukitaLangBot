package me.nasukhov.tukitalearner.bot.task

import me.nasukhov.tukitalearner.bot.bridge.IOResolver
import me.nasukhov.tukitalearner.bot.io.Channel
import me.nasukhov.tukitalearner.study.ProgressTracker
import me.nasukhov.tukitalearner.study.Time.isOffHours
import org.springframework.stereotype.Component

@Component("askQuestionTask")
class AskQuestion(
    private val progressTracker: ProgressTracker,
    private val ioResolver: IOResolver,
) : TaskRunner {
    override fun subscribesFor(): String = "ask_question"

    override fun runTask(task: Task) {
        check(subscribesFor() == task.name) { "Runner does not know how to execute the given task" }

        // We don't want it to work at night
        if (isOffHours) {
            return
        }

        ask(task.channel)
    }

    fun ask(channel: Channel) {
        val result = progressTracker.createRandomForChannel(channel)
        if (result.isEmpty) {
            return
        }

        val newQuestion = result.get()

        val replies = HashMap<String, String>()
        var optionNum = 0
        for (replyVariant in newQuestion.listVariants()) {
            replies.put(replyVariant, "qh answer $newQuestion.id ${++optionNum}")
        }

        val output = ioResolver.resolveFor(channel)
        output.promptChoice(newQuestion.text, replies)
    }
}
