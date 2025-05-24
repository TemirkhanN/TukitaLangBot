package me.nasukhov.TukitaLearner.bot.task

import me.nasukhov.TukitaLearner.bot.bridge.IOResolver
import me.nasukhov.TukitaLearner.bot.io.Channel
import me.nasukhov.TukitaLearner.study.ProgressTracker
import me.nasukhov.TukitaLearner.study.Time.isOffHours
import org.springframework.stereotype.Component

@Component("askQuestionTask")
class AskQuestion(
    private val progressTracker: ProgressTracker,
    private val ioResolver: IOResolver
) : TaskRunner {
    override fun subscribesFor(): String {
        return "ask_question"
    }

    override fun runTask(task: Task) {
        if (subscribesFor() != task.name) {
            throw RuntimeException("Runner does not know how to execute the given task")
        }

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
            replies.put(replyVariant, String.format("qh answer %s %d", newQuestion.id.toString(), ++optionNum))
        }

        val output = ioResolver.resolveFor(channel)
        output.promptChoice(newQuestion.text, replies)
    }
}
