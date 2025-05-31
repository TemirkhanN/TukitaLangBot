package me.nasukhov.bot.adapter.tg

import me.nasukhov.bot.Bot
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

@Component
class Telegram(
    @Value("\${tgbot.name}") val name: String,
    @Value("\${tgbot.token}") token: String,
    private val bot: Bot,
) : TelegramLongPollingBot(token) {
    // TODO KotlinLogging.logger{} is direly incompatible with my intellij idea
    // research what's going on there
    private val logger = LoggerFactory.getLogger(Telegram::class.java)

    fun run() {
        val telegramBotsApi = TelegramBotsApi(DefaultBotSession::class.java)
        telegramBotsApi.registerBot(this)
    }

    override fun onUpdateReceived(update: Update) {
        val tgInput = TelegramInput(update)
        val botInput = tgInput.convert()

        when {
            tgInput.isLeavingGroup() -> bot.leaveChannel(botInput.channel)
            tgInput.isJoiningGroup() -> bot.joinChannel(botInput.channel)
            !botInput.isEmpty -> {
                val result = bot.handle(botInput)
                getOutputHandler(tgInput.chatId)
                    .handle(result)
                    .onFailure {
                        logger.error(it.message)
                        if (it is NoAccessToChannelError) {
                            bot.leaveChannel(botInput.channel)
                        }
                    }
            }
        }
    }

    override fun getBotUsername(): String = name

    private fun getOutputHandler(chatId: Long): TelegramOutput = TelegramOutput(chatId, this)
}
