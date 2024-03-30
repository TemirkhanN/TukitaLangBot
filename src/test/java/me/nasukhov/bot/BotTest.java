package me.nasukhov.bot;

import me.nasukhov.bot.command.Handler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class BotTest {
    Bot bot;
    Output output;

    @BeforeEach
    void setup() {
        output = mock(Output.class);
        bot = new Bot();
    }

    @Test
    public void testHandleWhenNoHandlerIsPresent() {
        Input input = new Input(
                "Unrelated input",
                new Channel("SomeChannelId"),
                new User("SomeUserId", "SomeUserName")
        );

        bot.handle(input, output);

        verifyNoInteractions(output);
    }

    @Test
    public void testHandleWhenHandlerIsNotRegistered() {
        Input input = new Input(
                "/learn",
                new Channel("SomeChannelId"),
                new User("SomeUserId", "SomeUserName")
        );

        bot.handle(input, output);

        verifyNoInteractions(output);
    }

    @Test
    public void testHandle() {
        Input input = new Input(
                "/learn",
                new Channel("SomeChannelId"),
                new User("SomeUserId", "SomeUserName")
        );

        Handler handler = mock(Handler.class);
        when(handler.supports(input)).thenReturn(true);

        bot.addHandler(handler);

        bot.handle(input, output);

        verify(handler).supports(input);
        verify(handler).handle(input, output);
        verifyNoInteractions(output);
    }
}
