package me.nasukhov.tukitalearner.bot.bridge

import me.nasukhov.tukitalearner.bot.bridge.tg.Telegram
import me.nasukhov.tukitalearner.bot.bridge.tg.TelegramOutput
import me.nasukhov.tukitalearner.bot.io.Channel
import me.nasukhov.tukitalearner.bot.io.Output
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

// TODO injecting entire service locator is rather messy
@Component
class IOResolver(
    private val serviceLocator: ApplicationContext,
) {
    fun resolveFor(channel: Channel): Output {
        require(isTelegramGroup(channel)) {
            "Channel interaction is not supported yet."
        }

        val chatId = channel.id.substring(TG_PREFIX.length).toLong()

        return TelegramOutput(chatId, serviceLocator.getBean(Telegram::class.java))
    }

    companion object {
        const val TG_PREFIX: String = "tg_"

        private fun isTelegramGroup(channel: Channel): Boolean = channel.id.startsWith(TG_PREFIX)
    }
}
