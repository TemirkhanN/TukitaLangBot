package me.nasukhov.bot.bridge.tg;

import me.nasukhov.bot.*;
import me.nasukhov.bot.io.Channel;
import me.nasukhov.bot.io.Input;
import me.nasukhov.bot.io.User;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Telegram extends TelegramLongPollingBot {
    public final static String ID_PREFIX = "tg_";

    private final Bot bot;

    public Telegram(String token, Bot bot) {
        super(token);

        this.bot = bot;
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
        boolean isPublic;
        if (update.hasCallbackQuery()) {
            input = update.getCallbackQuery().getData();
            chatId = update.getCallbackQuery().getMessage().getChatId();
            userId = update.getCallbackQuery().getFrom().getId();
            name = update.getCallbackQuery().getFrom().getFirstName();
            isPublic = !update.getCallbackQuery().getMessage().isUserMessage();
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
            isPublic = !msg.isUserMessage();
        }

        bot.handle(
            new Input(input, getChannel(chatId, isPublic), getSender(userId, name)),
            new TelegramOutput(chatId, this)
        );
    }

    @Override
    public String getBotUsername() {
        return bot.getName();
    }

    // TODO See OutputResolver
    private Channel getChannel(Long chatId, boolean isPublic) {
        return new Channel(ID_PREFIX + chatId, isPublic);
    }

    private User getSender(Long userId, String name) {
        return new User(String.valueOf(userId), name);
    }
}
