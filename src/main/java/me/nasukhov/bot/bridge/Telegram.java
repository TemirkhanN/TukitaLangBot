package me.nasukhov.bot.bridge;

import me.nasukhov.bot.Bot;
import me.nasukhov.bot.Command;
import me.nasukhov.bot.Channel;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class Telegram extends TelegramLongPollingBot {
    private final Bot bot;

    // @Sensitive
    private final String botToken;

    public Telegram(String token, Bot bot) {
        this.bot = bot;
        botToken = token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage()) {
            return;
        }

        Message msg = update.getMessage();
        if (!msg.hasText()) {
            return;
        }

        bot.handle(new Command(msg.getText(), getSender(msg)));
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotUsername() {
        return bot.getName();
    }

    @Override
    public void onRegister() {
        // TODO notify about features
        super.onRegister();
    }

    private Channel getSender(Message msg) {
        Long chatId = msg.getChatId();

        return new Channel("tg" + chatId, new TelegramOutput(chatId, this));
    }
}
