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
        Command cmd = new Command(
                "Unrelated input",
                new Channel("SomeChannelId", output),
                new User("SomeUserId", "SomeUserName")
        );

        bot.handle(cmd);

        verifyNoInteractions(output);
    }

    @Test
    public void testHandleWhenHandlerIsNotRegistered() {
        Command cmd = new Command(
                "/learn",
                new Channel("SomeChannelId", output),
                new User("SomeUserId", "SomeUserName")
        );

        bot.handle(cmd);

        verifyNoInteractions(output);
    }

    @Test
    public void testHandle() {
        Command cmd = new Command(
                "/learn",
                new Channel("SomeChannelId", output),
                new User("SomeUserId", "SomeUserName")
        );

        Handler handler = mock(Handler.class);
        when(handler.supports(cmd)).thenReturn(true);

        bot.addHandler(handler);

        bot.handle(cmd);

        verify(handler).supports(cmd);
        verify(handler).handle(cmd);
        verifyNoInteractions(output);
    }
}
