package me.nasukhov.bot.adapter.tg

import me.nasukhov.bot.io.Output
import me.nasukhov.bot.io.PromptOutput
import me.nasukhov.bot.io.TextOutput
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Matcher
import java.util.regex.Pattern

internal class NoAccessToChannelError : Exception()

internal class TelegramOutput(
    private val chatId: Long,
    private val api: Telegram,
) {
    companion object {
        private val PLACEHOLDER_USERNAME: Pattern = Pattern.compile("<user>(-?\\d+)</user>")
        private val PLACEHOLDER_SPOILER: Pattern = Pattern.compile("<spoiler>(.+?)</spoiler>")
        private val userNames = ConcurrentHashMap<Long, String>()
    }

    fun handle(output: Output): Result<Unit> {
        if (output.isEmpty()) {
            return Result.success(Unit)
        }

        when (output) {
            is TextOutput -> {
                return write(output.value)
            }

            is PromptOutput -> {
                return promptChoice(output.question, output.replyOptions)
            }

            else -> return Result.failure(IllegalArgumentException(output::class.toString()))
        }
    }

    private fun write(text: String): Result<Unit> {
        val message = SendMessage()
        message.chatId = chatId.toString()
        message.text = renderText(text)
        message.disableNotification()
        message.parseMode = "HTML"
        try {
            api.execute(message)

            return Result.success(Unit)
        } catch (e: TelegramApiException) {
            return handleError(e)
        }
    }

    private fun promptChoice(
        question: String,
        replyOptions: Map<String, String>,
    ): Result<Unit> {
        val message = SendMessage()
        message.chatId = chatId.toString()
        message.text = question
        message.replyMarkup = createOptions(replyOptions)
        message.disableNotification()
        try {
            api.execute(message)

            return Result.success(Unit)
        } catch (e: TelegramApiException) {
            return handleError(e)
        }
    }

    private fun createOptions(options: Map<String, String>): InlineKeyboardMarkup {
        val inlineKeyboard = InlineKeyboardMarkup()
        val rowsInline = ArrayList<List<InlineKeyboardButton>>()
        for (entry in options.entries) {
            val button1 = InlineKeyboardButton()
            button1.text = entry.key
            // allows only 64bytes
            button1.callbackData = entry.value

            // One button per line
            val rowInline = ArrayList<InlineKeyboardButton>()
            rowInline.add(button1)
            rowsInline.add(rowInline)
        }

        inlineKeyboard.keyboard = rowsInline

        return inlineKeyboard
    }

    // TODO move to bb-codes rendering classes
    private fun renderText(text: String): String {
        // Id-to-Name renderer
        val matcher: Matcher = PLACEHOLDER_USERNAME.matcher(text)
        val result = StringBuilder()
        while (matcher.find()) {
            val userId = matcher.group(1).toLong()

            val name = getUsername(userId)
            if (name.isEmpty) {
                // This effectively means user had left the channel
                matcher.appendReplacement(result, "Unknown")
            } else {
                matcher.appendReplacement(result, name.get())
            }
        }
        matcher.appendTail(result)

        // Spoiler renderer
        val spoilerMatcher: Matcher = PLACEHOLDER_SPOILER.matcher(result.toString())
        val result2 = StringBuilder()
        while (spoilerMatcher.find()) {
            spoilerMatcher.appendReplacement(
                result2,
                "<span class=\"tg-spoiler\">" + spoilerMatcher.group(1) + "</span>",
            )
        }
        spoilerMatcher.appendTail(result2)

        return result2.toString()
    }

    private fun getUsername(userId: Long): Optional<String> {
        if (userNames.containsKey(userId)) {
            return Optional.of(userNames[userId]!!)
        }

        val softMaxSize = 10000
        // Just a safe-switch to prevent indefinite growth. Halves the map. Though, I'd rather remove stale entries
        if (userNames.size >= softMaxSize) {
            userNames.keys
                .take((userNames.size - softMaxSize) / 2)
                .forEach { userNames.remove(it) }
        }

        val command = GetChatMember()
        command.setChatId(chatId)
        command.userId = userId
        try {
            val name = api.execute(command).user.firstName
            userNames.put(userId, name)

            return Optional.of(name)
        } catch (_: TelegramApiException) {
            return Optional.empty()
        }
    }

    private fun handleError(error: TelegramApiException): Result<Unit> {
        if (error !is TelegramApiRequestException) {
            return Result.failure(error)
        }

        val errorCode = error.errorCode
        // Both cases mean that either bot no longer has access to chat
        if (errorCode != 400 && errorCode != 403) {
            return Result.failure(NoAccessToChannelError())
        }

        return Result.failure(error)
    }
}
