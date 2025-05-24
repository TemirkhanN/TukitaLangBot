package me.nasukhov.tukitalearner.bot.task

import me.nasukhov.tukitalearner.bot.bridge.IOResolver
import me.nasukhov.tukitalearner.study.Fact
import me.nasukhov.tukitalearner.study.Group
import me.nasukhov.tukitalearner.study.ProgressTracker
import org.springframework.stereotype.Component
import java.util.Optional

@Component
class ShareFact(
    private val progressTracker: ProgressTracker,
    private val ioResolver: IOResolver,
) : TaskRunner {
    override fun subscribesFor(): String = "share_fact"

    override fun runTask(task: Task) {
        val channel = task.channel
        val fact: Optional<Fact> = progressTracker.nextRandomFact(Group(channel.id))
        fact.ifPresent {
            ioResolver.resolveFor(channel).write(it.text)
        }
    }
}
