package me.nasukhov.bot.bridge;

import me.nasukhov.bot.Output;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramOutput implements Output {
    private final Long chatId;
    private final Telegram api;

    public TelegramOutput(Long chatId, Telegram tg) {
        this.chatId = chatId;
        api = tg;
    }

    @Override
    public void write(String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        try {
            api.execute(message);
        } catch (TelegramApiException e) {
            // TODO
            e.printStackTrace();
        }
    }
}
