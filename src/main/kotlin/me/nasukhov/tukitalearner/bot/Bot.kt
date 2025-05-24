package me.nasukhov.tukitalearner.bot

import me.nasukhov.tukitalearner.bot.command.Handler
import me.nasukhov.tukitalearner.bot.io.Input
import me.nasukhov.tukitalearner.bot.io.Output
import me.nasukhov.tukitalearner.bot.task.TaskManager
import org.springframework.stereotype.Component

@Component
class Bot(
    private val taskManager: TaskManager,
    initialHandlers: List<Handler>,
) {
    private val handlers: MutableList<Handler> = mutableListOf()

    init {
        initialHandlers.forEach { addHandler(it) }
    }

    fun addHandler(handler: Handler) {
        require(handlers.none { it::class == handler::class }) {
            "More than one handler of the same type is not allowed"
        }

        handlers.add(handler)
    }

    fun runTasks() {
        taskManager.run()
    }

    val name: String
        get() = "TukitaLangBot"

    // TODO remove output and use output resolver
    fun handle(
        command: Input,
        output: Output,
    ) {
        for (handler in handlers) {
            if (handler.supports(command)) {
                handler.handle(command, output)

                return
            }
        }
    }
}
