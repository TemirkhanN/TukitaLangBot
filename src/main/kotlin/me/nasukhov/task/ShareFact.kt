package me.nasukhov.task

import me.nasukhov.tukitalearner.study.Fact
import me.nasukhov.tukitalearner.study.Group
import me.nasukhov.tukitalearner.study.ProgressTracker
import java.util.*

// @Component
class ShareFact(
    private val progressTracker: ProgressTracker,
) : TaskRunner {
    override fun subscribesFor(): String = "share_fact"

    override fun runTask(task: Task) {
        val channel = task.getChannel()
        val fact: Optional<Fact> = progressTracker.nextRandomFact(Group(channel.id))
        fact.ifPresent {
            // TODO
            // write(it.text)
        }
    }
}
