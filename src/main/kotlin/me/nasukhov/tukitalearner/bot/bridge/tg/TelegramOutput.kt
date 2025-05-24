package me.nasukhov.tukitalearner.bot.bridge.tg

import me.nasukhov.tukitalearner.bot.io.Output
import org.slf4j.LoggerFactory
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

class TelegramOutput(
    private val chatId: Long,
    private val api: Telegram,
) : Output {
    // TODO KotlinLogging.logger{} is direly incompatible with my intellij idea
    // research what's going on there
    private val logger = LoggerFactory.getLogger(TelegramOutput::class.java)

    override fun write(text: String) {
        val message = SendMessage()
        message.chatId = chatId.toString()
        message.text = renderText(text)
        message.disableNotification()
        message.parseMode = "HTML"
        try {
            api.execute(message)
        } catch (e: TelegramApiException) {
            if (handleError(e)) {
                return
            }
            logger.error(e.message, e)
        }
    }

    override fun promptChoice(
        question: String,
        replyOptions: Map<String, String>,
    ) {
        val message = SendMessage()
        message.chatId = chatId.toString()
        message.text = question
        message.replyMarkup = createOptions(replyOptions)
        message.disableNotification()
        try {
            api.execute(message)
        } catch (e: TelegramApiException) {
            if (handleError(e)) {
                return
            }
            logger.error(e.message, e)
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

    private fun handleError(error: TelegramApiException): Boolean {
        if (error !is TelegramApiRequestException) {
            return false
        }

        // TODO SRP violation event dispatch event so handler deactivates it
        val errorCode = error.errorCode
        // Both cases mean that either bot no longer has access to chat
        if (errorCode != 400 && errorCode != 403) {
            return false
        }

        // TODO IOResolver.telegramChannel(chatId, true).deactivate();
        return true
    }

    companion object {
        private val PLACEHOLDER_USERNAME: Pattern = Pattern.compile("<user>(-?\\d+)</user>")
        private val PLACEHOLDER_SPOILER: Pattern = Pattern.compile("<spoiler>(.+?)</spoiler>")
        private val userNames = ConcurrentHashMap<Long, String>()
    }
}
