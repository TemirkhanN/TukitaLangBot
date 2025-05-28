package me.nasukhov.tukitalearner.bot

import me.nasukhov.tukitalearner.bot.command.Handler
import me.nasukhov.tukitalearner.bot.io.Channel
import me.nasukhov.tukitalearner.bot.io.Input
import me.nasukhov.tukitalearner.bot.io.Output
import me.nasukhov.tukitalearner.bot.task.TaskManager
import me.nasukhov.tukitalearner.study.Group
import me.nasukhov.tukitalearner.study.GroupRepository
import org.springframework.stereotype.Component
import java.util.function.Supplier

@Component
class Bot(
    private val taskManager: TaskManager,
    private val groupRepository: GroupRepository,
    initialHandlers: List<Handler>,
) {
    val name = "TukitaLangBot"

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

    fun activateGroup(channel: Channel) {
        val result = groupRepository.findById(channel.id)

        val group = result.orElseGet { Group(channel.id) }
        group.activate()
        groupRepository.save(group)
    }

    fun deactivateGroup(channel: Channel) {
        val result = groupRepository.findById(channel.id)
        if (result.isEmpty) {
            return
        }

        // TODO activation and deactivation of the group doesn't belong to the bot itself
        val group = result.get()
        group.deactivate()
        groupRepository.save(group)
    }

    // bot run
    // listen for events by tgBridge
    // message received by tgBridge
    // tgBridge convert message to local IO and pass down the pipeline
    // TODO remove output and use output resolver
    fun handle(
        command: Input,
        output: Output,
    ) {
        val channelId = command.channel.id
        val channel =
            groupRepository.findById(channelId).orElseGet(
                Supplier {
                    val newChannel = Group(channelId)
                    groupRepository.save(newChannel)
                    // TODO register tasks TaskManager.registerTasks
                    newChannel
                },
            )

        if (!channel.isActive) {
            return
        }

        for (handler in handlers) {
            if (handler.supports(command)) {
                handler.handle(command, output)

                return
            }
        }
    }
}
