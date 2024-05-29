package me.nasukhov.bot;

import me.nasukhov.DI.ServiceLocator;
import me.nasukhov.bot.command.Handler;
import me.nasukhov.bot.io.*;
import me.nasukhov.bot.task.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.mockito.Mockito.*;

public class BotTest {
    Bot bot;
    Output output;
    ChannelRepository channelRepository;

    @BeforeEach
    void setup() {
        output = mock(Output.class);
        channelRepository = mock(ChannelRepository.class);
        bot = new Bot(channelRepository, mock(TaskManager.class));
        ServiceLocator.resetInstance();
        ServiceLocator locator = ServiceLocator.getInstance();
        locator.addDefinition(ChannelRepository.class, channelRepository);
    }

    @Test
    public void testHandleWhenChannelIsInactive(){
        Input input = new Input(
                "Unrelated input",
                new Channel("SomeChannelId"),
                new User("SomeUserId", "SomeUserName")
        );

        when(channelRepository.findById(input.channel().id)).thenReturn(Optional.of(input.channel()));
        when(channelRepository.isActive(input.channel())).thenReturn(false);

        bot.handle(input, output);

        verify(channelRepository).findById(input.channel().id);
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

        when(channelRepository.findById(input.channel().id)).thenReturn(Optional.of(input.channel()));
        when(channelRepository.isActive(input.channel())).thenReturn(true);

        bot.handle(input, output);

        verify(channelRepository).findById(input.channel().id);
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

        when(channelRepository.findById(input.channel().id)).thenReturn(Optional.of(input.channel()));
        when(channelRepository.isActive(input.channel())).thenReturn(true);

        bot.handle(input, output);

        verify(channelRepository).findById(input.channel().id);
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

        when(channelRepository.findById(input.channel().id)).thenReturn(Optional.of(input.channel()));
        when(channelRepository.isActive(input.channel())).thenReturn(true);
        when(handler.supports(input)).thenReturn(true);

        bot.handle(input, output);

        verify(channelRepository).findById(input.channel().id);
        verify(channelRepository).isActive(input.channel());
        verify(handler).supports(input);
        verify(handler).handle(input, output);
        verifyNoInteractions(output);
    }

    @Test
    public void testHandleForNonRegisteredChannel() {
        Input input = new Input(
                "/learn",
                new Channel("SomeChannelId"),
                new User("SomeUserId", "SomeUserName")
        );

        Handler handler = mock(Handler.class);
        bot.addHandler(handler);

        when(channelRepository.findById(input.channel().id)).thenReturn(Optional.empty());
        doNothing().when(channelRepository).saveChannel(input.channel());
        when(handler.supports(input)).thenReturn(true);

        bot.handle(input, output);

        verify(channelRepository).findById(input.channel().id);
        verify(channelRepository).saveChannel(input.channel());
        verify(handler).supports(input);
        verify(handler).handle(input, output);
        verifyNoInteractions(output);
    }
}
