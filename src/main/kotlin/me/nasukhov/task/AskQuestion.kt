package me.nasukhov.task

import me.nasukhov.bot.io.Channel
import me.nasukhov.bot.io.PromptOutput
import me.nasukhov.tukitalearner.study.GroupRepository
import me.nasukhov.tukitalearner.study.ProgressTracker
import me.nasukhov.tukitalearner.study.Time.isOffHours

// @Component("askQuestionTask")
class AskQuestion(
    private val progressTracker: ProgressTracker,
    private val groupRepository: GroupRepository,
) : TaskRunner {
    override fun subscribesFor(): String = "ask_question"

    override fun runTask(task: Task) {
        check(subscribesFor() == task.name) { "Runner does not know how to execute the given task" }

        // We don't want it to work at night
        if (isOffHours) {
            return
        }

        ask(task.getChannel())
    }

    fun ask(channel: Channel) {
        val group = groupRepository.findById(channel.id)
        val result = progressTracker.createRandomForGroup(group!!)
        if (result.isEmpty) {
            return
        }

        val newQuestion = result.get()

        val replies = HashMap<String, String>()
        var optionNum = 0
        for (replyVariant in newQuestion.listVariants()) {
            replies.put(replyVariant, "qh answer $newQuestion.id ${++optionNum}")
        }

        // TODO
        PromptOutput(newQuestion.text, replies)
    }
}
