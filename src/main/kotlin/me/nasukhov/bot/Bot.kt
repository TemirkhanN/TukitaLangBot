package me.nasukhov.bot

import me.nasukhov.bot.command.Handler
import me.nasukhov.bot.event.JoinedChannel
import me.nasukhov.bot.event.LeftChannel
import me.nasukhov.bot.event.ReceivedInput
import me.nasukhov.bot.io.Channel
import me.nasukhov.bot.io.Input
import me.nasukhov.bot.io.NoOutput
import me.nasukhov.bot.io.Output
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class Bot(
    initialHandlers: List<Handler>,
    private val eventDispatcher: ApplicationEventPublisher,
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

    fun joinChannel(channel: Channel) {
        eventDispatcher.publishEvent(JoinedChannel(channel))
    }

    fun leaveChannel(channel: Channel) {
        eventDispatcher.publishEvent(LeftChannel(channel))
    }

    fun handle(command: Input): Output {
        eventDispatcher.publishEvent(ReceivedInput(command))

        for (handler in handlers) {
            if (handler.supports(command)) {
                return handler.handle(command)
            }
        }

        return NoOutput()
    }
}
