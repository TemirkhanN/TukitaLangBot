package me.nasukhov.bot

import me.nasukhov.bot.command.Handler
import me.nasukhov.bot.io.Channel
import me.nasukhov.bot.io.Input
import me.nasukhov.bot.io.NoOutput
import me.nasukhov.bot.io.TextOutput
import me.nasukhov.bot.io.User
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertInstanceOf
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.springframework.context.ApplicationEventPublisher
import kotlin.test.assertSame

class BotTest {
    private lateinit var bot: Bot

    @BeforeEach
    fun setup() {
        bot =
            Bot(
                mutableListOf(),
                Mockito.mock(ApplicationEventPublisher::class.java),
            )
    }

    @Test
    fun testHandleWhenNoHandlerIsPresent() {
        val channel = Channel("SomeChannelId")
        val user = User("SomeUserId", "SomeUserName")
        val input = Input("Unrelated input", channel, user)

        val result = bot.handle(input)
        assertInstanceOf<NoOutput>(result)
    }

    @Test
    fun testHandleUnsupportedCommand() {
        val channel = Channel("SomeChannelId")
        val user = User("SomeUserId", "SomeUserName")
        val input = Input("Unrelated input", channel, user)

        val handler = Mockito.mock(Handler::class.java)
        Mockito.`when`(handler.supports(input)).thenReturn(false)
        bot.addHandler(handler)

        val result = bot.handle(input)
        assertInstanceOf<NoOutput>(result)

        Mockito
            .verify(handler, Mockito.never())
            .handle(any())
    }

    @Test
    fun testHandle() {
        val channel = Channel("SomeChannelId")
        val user = User("SomeUserId", "SomeUserName")
        val input = Input("Unrelated input", channel, user)

        val handler = Mockito.mock(Handler::class.java)
        Mockito.`when`(handler.supports(input)).thenReturn(true)
        val handlerOutput = TextOutput("Some output")
        Mockito.`when`(handler.handle(input)).thenReturn(handlerOutput)

        bot.addHandler(handler)

        val result = bot.handle(input)
        assertSame(handlerOutput, result)
    }
}
