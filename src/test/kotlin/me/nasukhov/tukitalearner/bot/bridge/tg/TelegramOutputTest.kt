package me.nasukhov.tukitalearner.bot.bridge.tg

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

class TelegramOutputTest {
    private var chatId: Long = 123L
    private lateinit var api: Telegram
    private lateinit var output: TelegramOutput

    @BeforeEach
    fun setup() {
        api = Mockito.mock(Telegram::class.java)
        output = TelegramOutput(chatId, api)
    }

    @Test
    @Throws(TelegramApiException::class)
    fun testTextRendering() {
        val arg = ArgumentCaptor.forClass(SendMessage::class.java)

        Mockito.`when`(api.execute(arg.capture())).thenReturn(Message())
        output.write("Hey 3123451 here is your code <spoiler>some spoiler text</spoiler>")

        Mockito.verify(api).execute(arg.capture())

        Assertions.assertEquals(chatId.toString(), arg.getValue()!!.chatId)
        Assertions.assertTrue(arg.value!!.disableNotification)
        Assertions.assertEquals(
            "Hey 3123451 here is your code <span class=\"tg-spoiler\">some spoiler text</span>",
            arg.value!!.text,
        )
    }
}
