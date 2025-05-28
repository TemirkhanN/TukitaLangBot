package me.nasukhov.tukitalearner.bot.bridge.tg

import me.nasukhov.tukitalearner.bot.Bot
import me.nasukhov.tukitalearner.bot.bridge.IOResolver
import me.nasukhov.tukitalearner.bot.io.Channel
import me.nasukhov.tukitalearner.bot.io.Input
import me.nasukhov.tukitalearner.bot.io.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

@Component
class Telegram(
    @Value("\${tgbot.token}") token: String,
    private val bot: Bot,
    private val io: IOResolver,
) : TelegramLongPollingBot(token) {
    fun run() {
        val telegramBotsApi = TelegramBotsApi(DefaultBotSession::class.java)
        telegramBotsApi.registerBot(this)
        bot.runTasks()
    }

    override fun onUpdateReceived(update: Update) {
        val input: String
        val channel: Channel
        val userId: Long
        val name: String

        // TODO provide some wrapper around Update to make encapsulation adequate. Current one is very broad and messy
        if (update.hasCallbackQuery()) {
            input = update.callbackQuery.data
            channel = getChannel(update.callbackQuery.message.chatId)
            userId = update.callbackQuery.from.id
            name = update.callbackQuery.from.firstName
        } else {
            if (!update.hasMessage()) {
                handleSystemEvent(update)

                return
            }

            val msg = update.message
            if (!msg.hasText()) {
                return
            }
            input = msg.text
            channel = getChannel(msg.chatId)
            userId = msg.from.id
            name = msg.from.firstName
        }

        bot.handle(
            Input(input, channel, getSender(userId, name)),
            io.resolveFor(channel),
        )
    }

    override fun getBotUsername(): String = bot.name

    private fun getSender(
        userId: Long,
        name: String,
    ): User = User(userId.toString(), name)

    private fun isRemovedFromGroup(action: Update): Boolean {
        val membershipUpdate = action.myChatMember
        if (membershipUpdate == null) {
            return false
        }

        return membershipUpdate.newChatMember.status == "left"
    }

    private fun isAddedToGroup(action: Update): Boolean {
        val membershipUpdate = action.myChatMember
        if (membershipUpdate == null) {
            return false
        }

        return membershipUpdate.newChatMember.status == "member"
    }

    private fun getChannel(chatId: Long): Channel = Channel(IOResolver.TG_PREFIX + chatId)

    private fun handleSystemEvent(update: Update) {
        if (isRemovedFromGroup(update)) {
            bot.deactivateGroup(getChannel(update.myChatMember.chat.id))

            return
        }

        if (isAddedToGroup(update)) {
            bot.activateGroup(getChannel(update.myChatMember.chat.id))
        }
    }
}
