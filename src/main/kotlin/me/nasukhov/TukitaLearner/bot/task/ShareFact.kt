package me.nasukhov.TukitaLearner.bot.task

import me.nasukhov.TukitaLearner.bot.bridge.IOResolver
import me.nasukhov.TukitaLearner.study.Fact
import me.nasukhov.TukitaLearner.study.Group
import me.nasukhov.TukitaLearner.study.ProgressTracker
import org.springframework.stereotype.Component
import java.util.Optional

@Component
class ShareFact(private val progressTracker: ProgressTracker, private val ioResolver: IOResolver) : TaskRunner {
    override fun subscribesFor(): String {
        return "share_fact"
    }

    override fun runTask(task: Task) {
        val channel = task.channel
        val fact: Optional<Fact> = progressTracker.nextRandomFact(Group(channel.id))
        fact.ifPresent {
            ioResolver.resolveFor(channel).write(it.text)
        }
    }
}
