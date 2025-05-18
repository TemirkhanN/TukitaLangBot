package me.nasukhov.TukitaLearner.bot;

import me.nasukhov.TukitaLearner.bot.command.Handler;
import me.nasukhov.TukitaLearner.bot.io.*;
import me.nasukhov.TukitaLearner.bot.task.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

public class BotTest {
    Bot bot;
    Output output;

    @BeforeEach
    void setup() {
        output = mock(Output.class);
        bot = new Bot(mock(TaskManager.class), List.of());
    }

    @Test
    public void testHandleWhenNoHandlerIsPresent() {
        var channel = new Channel("SomeChannelId");
        var user =  new User("SomeUserId", "SomeUserName");
        Input input = new Input("Unrelated input", channel, user);

        bot.handle(input, output);
        verifyNoInteractions(output);
    }

    @Test
    public void testHandleUnsupportedCommand() {
        var channel = new Channel("SomeChannelId");
        var user =  new User("SomeUserId", "SomeUserName");
        Input input = new Input("Unrelated input", channel, user);

        Handler handler = mock(Handler.class);
        when(handler.supports(input)).thenReturn(false);
        bot.addHandler(handler);

        bot.handle(input, output);

        verify(handler, never()).handle(any(), any());
    }

    @Test
    public void testHandle() {
        var channel = new Channel("SomeChannelId");
        var user =  new User("SomeUserId", "SomeUserName");
        Input input = new Input("Unrelated input", channel, user);

        Handler handler = mock(Handler.class);
        when(handler.supports(input)).thenReturn(true);
        bot.addHandler(handler);

        bot.handle(input, output);

        verify(handler).handle(input, output);
        verifyNoInteractions(output);
    }
}
