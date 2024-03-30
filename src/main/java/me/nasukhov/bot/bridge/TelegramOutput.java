package me.nasukhov.bot.bridge;

import me.nasukhov.bot.Output;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        message.disableNotification();
        try {
            api.execute(message);
        } catch (TelegramApiException e) {
            // TODO
            e.printStackTrace();
        }
    }

    @Override
    public void promptChoice(String question, Map<String, String> replyOptions) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(question);
        message.setReplyMarkup(createOptions(replyOptions));
        message.disableNotification();
        try {
            api.execute(message);
        } catch (TelegramApiException e) {
            // TODO
            e.printStackTrace();
        }
    }

    private InlineKeyboardMarkup createOptions(Map<String, String> options) {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        for (Map.Entry<String, String> entry : options.entrySet()) {
            InlineKeyboardButton button1 = new InlineKeyboardButton();
            button1.setText(entry.getKey());
            // allows only 64bytes
            button1.setCallbackData(entry.getValue());

            // One button per line
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            rowInline.add(button1);
            rowsInline.add(rowInline);
        }

        inlineKeyboard.setKeyboard(rowsInline);

        return inlineKeyboard;
    }
}
