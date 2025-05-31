package me.nasukhov.bot.adapter.tg

import me.nasukhov.bot.io.Channel
import me.nasukhov.bot.io.Input
import me.nasukhov.bot.io.User
import org.telegram.telegrambots.meta.api.objects.Update

internal class TelegramInput(
    private val update: Update,
) {
    val chatId: Long
    private val senderId: Long?
    private val senderName: String?
    private val msg: String?

    val isSystemEvent: Boolean

    init {
        when {
            update.hasCallbackQuery() -> {
                isSystemEvent = false
                msg = update.callbackQuery.data
                chatId = update.callbackQuery.message.chatId
                senderId = update.callbackQuery.from.id
                senderName = update.callbackQuery.from.firstName
            }

            update.message.hasText() -> {
                isSystemEvent = false
                msg = update.message.text
                chatId = update.message.chatId
                senderId = update.message.from.id
                senderName = update.message.from.firstName
            }

            else -> {
                isSystemEvent = true
                chatId = update.myChatMember.chat.id
                msg = null
                senderId = null
                senderName = null
            }
        }
    }

    companion object {
        private const val CHANNEL_PREFIX: String = "tg_"
    }

    fun isLeavingGroup(): Boolean {
        if (!isSystemEvent) {
            return false
        }

        val membershipUpdate = update.myChatMember
        if (membershipUpdate == null) {
            return false
        }

        return membershipUpdate.newChatMember.status == "left"
    }

    fun isJoiningGroup(): Boolean {
        if (!isSystemEvent) {
            return false
        }

        val membershipUpdate = update.myChatMember
        if (membershipUpdate == null) {
            return false
        }

        return membershipUpdate.newChatMember.status == "member"
    }

    fun convert(): Input {
        if (isSystemEvent) {
            return Input(
                raw = "",
                channel = Channel(CHANNEL_PREFIX + chatId),
                sender = User.system,
            )
        }

        return Input(
            raw = msg!!,
            channel = Channel(CHANNEL_PREFIX + chatId),
            sender = User(senderId!!.toString(), senderName!!),
        )
    }
}
