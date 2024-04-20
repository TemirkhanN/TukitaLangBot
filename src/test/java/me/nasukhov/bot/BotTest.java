package me.nasukhov.bot;

import me.nasukhov.bot.command.Handler;
import me.nasukhov.bot.io.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class BotTest {
    Bot bot;
    Output output;
    ChannelRepository channelRepository;

    @BeforeEach
    void setup() {
        output = mock(Output.class);
        channelRepository = mock(ChannelRepository.class);
        bot = new Bot(channelRepository);
    }

    @Test
    public void testHandleWhenChannelIsInactive(){
        Input input = new Input(
                "Unrelated input",
                new Channel("SomeChannelId"),
                new User("SomeUserId", "SomeUserName")
        );

        when(channelRepository.isActive(input.channel())).thenReturn(false);

        bot.handle(input, output);

        verify(channelRepository).isActive(input.channel());
        verifyNoMoreInteractions(output, channelRepository);
    }

    @Test
    public void testHandleWhenNoHandlerIsPresent() {
        Input input = new Input(
                "Unrelated input",
                new Channel("SomeChannelId"),
                new User("SomeUserId", "SomeUserName")
        );

        when(channelRepository.isActive(input.channel())).thenReturn(true);

        bot.handle(input, output);

        verify(channelRepository).isActive(input.channel());
        verifyNoMoreInteractions(output, channelRepository);
    }

    @Test
    public void testHandleWhenHandlerIsNotRegistered() {
        Input input = new Input(
                "/learn",
                new Channel("SomeChannelId"),
                new User("SomeUserId", "SomeUserName")
        );

        when(channelRepository.isActive(input.channel())).thenReturn(true);

        bot.handle(input, output);

        verify(channelRepository).isActive(input.channel());
        verifyNoMoreInteractions(output, channelRepository);
    }

    @Test
    public void testHandle() {
        Input input = new Input(
                "/learn",
                new Channel("SomeChannelId"),
                new User("SomeUserId", "SomeUserName")
        );

        Handler handler = mock(Handler.class);
        bot.addHandler(handler);

        when(channelRepository.isActive(input.channel())).thenReturn(true);
        when(handler.supports(input)).thenReturn(true);

        bot.handle(input, output);

        verify(channelRepository).isActive(input.channel());
        verify(handler).supports(input);
        verify(channelRepository).addChannel(input.channel());
        verify(handler).handle(input, output);
        verifyNoInteractions(output);
    }
}
