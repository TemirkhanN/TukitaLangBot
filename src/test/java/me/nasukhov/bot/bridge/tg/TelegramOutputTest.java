package me.nasukhov.bot.bridge.tg;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.ArgumentCaptor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.mockito.Mockito.*;

public class TelegramOutputTest {
    Long chatId = 123L;
    Telegram api;
    TelegramOutput output;

    @BeforeEach
    public void setup() {
        api = mock(Telegram.class);
        output = new TelegramOutput(chatId, api);
    }

    @Test
    public void testTextRendering() throws TelegramApiException {
        ArgumentCaptor<SendMessage> arg = ArgumentCaptor.forClass(SendMessage.class);
        /*

        ChatMemberMember member = new ChatMemberMember();
        User u = new User();
        u.setFirstName("SomeFirstName");
        member.setUser(u);

        GetChatMember getMemberCommand = new GetChatMember();
        getMemberCommand.setChatId(chatId);
        getMemberCommand.setUserId(3123451L);
        when(api.execute(getMemberCommand)).thenReturn(member);
        */
        when(api.execute(arg.capture())).thenReturn(new Message());
        output.write("Hey 3123451 here is your code <spoiler>some spoiler text</spoiler>");

        // verify(api).execute(getMemberCommand);
        verify(api).execute(arg.capture());

        Assertions.assertEquals(chatId.toString(), arg.getValue().getChatId());
        Assertions.assertTrue(arg.getValue().getDisableNotification());
        Assertions.assertEquals("Hey 3123451 here is your code <span class=\"tg-spoiler\">some spoiler text</span>", arg.getValue().getText());
    }
}
