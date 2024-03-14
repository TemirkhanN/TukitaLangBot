package me.nasukhov.bot.bridge;

import me.nasukhov.bot.Bot;
import me.nasukhov.bot.Command;
import me.nasukhov.bot.Channel;
import me.nasukhov.bot.User;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Telegram extends TelegramLongPollingBot {
    final static String ID_PREFIX = "tg_";

    private final Bot bot;

    // @Sensitive
    private final String botToken;

    public Telegram(String token, Bot bot) {
        this.bot = bot;
        botToken = token;
    }

    public void run() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(this);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        } catch (Exception e) {
            // TODO
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        String input;
        Long chatId;
        Long userId;
        String name;
        if (update.hasCallbackQuery()) {
            input = update.getCallbackQuery().getData();
            chatId = update.getCallbackQuery().getMessage().getChatId();
            userId = update.getCallbackQuery().getFrom().getId();
            name = update.getCallbackQuery().getFrom().getFirstName();
        } else {
            if (!update.hasMessage()) {
                return;
            }

            Message msg = update.getMessage();
            if (!msg.hasText()) {
                return;
            }
            input = msg.getText();
            chatId = msg.getChatId();
            userId = msg.getFrom().getId();
            name = msg.getFrom().getFirstName();
        }

        bot.handle(new Command(input, getChannel(chatId), getSender(userId, name)));
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

    private Channel getChannel(Long chatId) {
        return new Channel(ID_PREFIX + chatId, new TelegramOutput(chatId, this));
    }

    private User getSender(Long userId, String name) {
        return new User(String.valueOf(userId), name);
    }
}
