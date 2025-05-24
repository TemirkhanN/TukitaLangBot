package me.nasukhov.TukitaLearner.bot.bridge.tg

import me.nasukhov.TukitaLearner.bot.Bot
import me.nasukhov.TukitaLearner.bot.bridge.IOResolver
import me.nasukhov.TukitaLearner.bot.io.Channel
import me.nasukhov.TukitaLearner.bot.io.ChannelRepository
import me.nasukhov.TukitaLearner.bot.io.Input
import me.nasukhov.TukitaLearner.bot.io.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import java.util.function.Supplier

@Component
class Telegram(
    @Value("\${BOT_TKT_TG_TOKEN}") token: String,
    private val bot: Bot,
    private val channelRepository: ChannelRepository,
    private val io: IOResolver
) : TelegramLongPollingBot(token) {
    fun run() {
        try {
            val telegramBotsApi = TelegramBotsApi(DefaultBotSession::class.java)
            telegramBotsApi.registerBot(this)
            bot.runTasks()
        } catch (e: TelegramApiException) {
            throw RuntimeException(e)
        }
    }

    override fun onUpdateReceived(update: Update) {
        val input: String
        val channelId: String
        val userId: Long
        val name: String
        val isPublic: Boolean

        // TODO provide some wrapper around Update to make encapsulation adequate. Current one is very broad and messy
        if (update.hasCallbackQuery()) {
            input = update.callbackQuery.data
            channelId = getChannelId(update.callbackQuery.message.chatId)
            userId = update.callbackQuery.from.id
            name = update.callbackQuery.from.firstName
            isPublic = !update.callbackQuery.message.isUserMessage
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
            channelId = getChannelId(msg.chatId)
            userId = msg.from.id
            name = msg.from.firstName
            isPublic = !msg.isUserMessage
        }

        val channel = channelRepository.findById(channelId).orElseGet(Supplier {
            val newChannel = Channel(channelId, isPublic)
            channelRepository.save(newChannel)
            // TODO register tasks TaskManager.registerTasks
            newChannel
        })

        if (!channel.isActive) {
            return
        }

        bot.handle(
            Input(input, channel, getSender(userId, name)),
            io.resolveFor(channel)
        )
    }

    override fun getBotUsername(): String {
        return bot.name
    }

    private fun getSender(userId: Long, name: String): User {
        return User(userId.toString(), name)
    }

    private fun isRemovedFromChannel(action: Update): Boolean {
        val membershipUpdate = action.myChatMember
        if (membershipUpdate == null) {
            return false
        }

        return membershipUpdate.newChatMember.status == "left"
    }

    private fun isAddedToChannel(action: Update): Boolean {
        val membershipUpdate = action.myChatMember
        if (membershipUpdate == null) {
            return false
        }

        return membershipUpdate.newChatMember.status == "member"
    }

    private fun getChannelId(chatId: Long): String {
        return IOResolver.TG_PREFIX + chatId
    }

    private fun deactivateChannel(chatId: Long) {
        val result = channelRepository.findById(getChannelId(chatId))
        if (result.isEmpty) {
            return
        }

        val channel = result.get()
        channel.deactivate()
        channelRepository.save(channel)
    }

    private fun activateChannel(chatId: Long, isPublic: Boolean) {
        val channelId = getChannelId(chatId)
        val result = channelRepository.findById(channelId)

        val channel = result.orElseGet { Channel(channelId, isPublic) }
        channel.activate()
        channelRepository.save(channel)
    }

    private fun handleSystemEvent(update: Update) {
        if (isRemovedFromChannel(update)) {
            deactivateChannel(update.myChatMember.chat.id)

            return
        }

        if (isAddedToChannel(update)) {
            activateChannel(
                update.myChatMember.chat.id,
                !update.myChatMember.chat.isUserChat
            )
        }
    }
}
