package me.nasukhov.TukitaLearner.bot.bridge

import me.nasukhov.TukitaLearner.bot.bridge.tg.Telegram
import me.nasukhov.TukitaLearner.bot.bridge.tg.TelegramOutput
import me.nasukhov.TukitaLearner.bot.io.Channel
import me.nasukhov.TukitaLearner.bot.io.Output
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

// TODO injecting entire service locator is rather messy
@Component
class IOResolver(
    private val serviceLocator: ApplicationContext
) {
    fun resolveFor(channel: Channel): Output {
        if (isTelegramChannel(channel)) {
            val chatId = channel.id.substring(TG_PREFIX.length).toLong()

            return TelegramOutput(chatId, serviceLocator.getBean(Telegram::class.java))
        }

        throw RuntimeException("Channel interaction is not supported. Probably some mistake in the code.")
    }

    companion object {
        const val TG_PREFIX: String = "tg_"

        private fun isTelegramChannel(channel: Channel): Boolean {
            return channel.id.startsWith(TG_PREFIX)
        }
    }
}
