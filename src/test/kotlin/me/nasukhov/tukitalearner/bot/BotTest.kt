package me.nasukhov.tukitalearner.bot

import me.nasukhov.tukitalearner.bot.command.Handler
import me.nasukhov.tukitalearner.bot.io.Channel
import me.nasukhov.tukitalearner.bot.io.Input
import me.nasukhov.tukitalearner.bot.io.Output
import me.nasukhov.tukitalearner.bot.io.User
import me.nasukhov.tukitalearner.bot.task.TaskManager
import me.nasukhov.tukitalearner.study.Group
import me.nasukhov.tukitalearner.study.GroupRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.any

class BotTest {
    private lateinit var bot: Bot
    private lateinit var output: Output

    @BeforeEach
    fun setup() {
        output = Mockito.mock(Output::class.java)
        bot = Bot(
            Mockito.mock(TaskManager::class.java),
            Mockito.mock(GroupRepository::class.java),
            mutableListOf()
        )
    }

    @Test
    fun testHandleWhenNoHandlerIsPresent() {
        val channel = Channel("SomeChannelId")
        val user = User("SomeUserId", "SomeUserName")
        val input = Input("Unrelated input", channel, user)

        bot.handle(input, output)
        Mockito.verifyNoInteractions(output)
    }

    @Test
    fun testHandleUnsupportedCommand() {
        val channel = Channel("SomeChannelId")
        val user = User("SomeUserId", "SomeUserName")
        val input = Input("Unrelated input", channel, user)

        val handler = Mockito.mock(Handler::class.java)
        Mockito.`when`(handler.supports(input)).thenReturn(false)
        bot.addHandler(handler)

        bot.handle(input, output)

        Mockito
            .verify(handler, Mockito.never())
            .handle(any(), any())
    }

    @Test
    fun testHandle() {
        val channel = Channel("SomeChannelId")
        val user = User("SomeUserId", "SomeUserName")
        val input = Input("Unrelated input", channel, user)

        val handler = Mockito.mock(Handler::class.java)
        Mockito.`when`(handler.supports(input)).thenReturn(true)
        bot.addHandler(handler)

        bot.handle(input, output)

        Mockito.verify(handler).handle(input, output)
        Mockito.verifyNoMoreInteractions(output)
    }
}
