package me.nasukhov.TukitaLearner.bot.bridge.tg;

import me.nasukhov.TukitaLearner.bot.*;
import me.nasukhov.TukitaLearner.bot.bridge.IOResolver;
import me.nasukhov.TukitaLearner.bot.io.*;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Telegram extends TelegramLongPollingBot {
    private final Bot bot;

    public Telegram(String token, Bot bot) {
        super(token);

        this.bot = bot;
    }

    public void run() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(this);
            bot.runTasks();
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
        // TODO provide some wrapper around Update to make encapsulation adequate. Current one is very broad and messy
        if (update.hasCallbackQuery()) {
            input = update.getCallbackQuery().getData();
            chatId = update.getCallbackQuery().getMessage().getChatId();
            userId = update.getCallbackQuery().getFrom().getId();
            name = update.getCallbackQuery().getFrom().getFirstName();
            isPublic = !update.getCallbackQuery().getMessage().isUserMessage();
        } else {
            if (!update.hasMessage()) {
                if (isRemovedFromChannel(update)) {
                    Channel channel = IOResolver.telegramChannel(
                            update.getMyChatMember().getChat().getId(),
                            !update.getMyChatMember().getChat().isUserChat()
                    );

                    channel.deactivate();
                } else if (isAddedToChannel(update)) {
                    Channel channel = IOResolver.telegramChannel(
                            update.getMyChatMember().getChat().getId(),
                            !update.getMyChatMember().getChat().isUserChat()
                    );

                    channel.activate();
                }

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
            new Input(input, IOResolver.telegramChannel(chatId, isPublic), getSender(userId, name)),
            new TelegramOutput(chatId, this)
        );
    }

    @Override
    public String getBotUsername() {
        return bot.getName();
    }

    private User getSender(Long userId, String name) {
        return new User(String.valueOf(userId), name);
    }

    private boolean isRemovedFromChannel(Update action) {
        ChatMemberUpdated membershipUpdate = action.getMyChatMember();
        if (membershipUpdate == null) {
            return false;
        }

        // Bruh, either java or tg-api-sdk hates LoD
        return membershipUpdate.getNewChatMember().getStatus().equals("left");
    }

    private boolean isAddedToChannel(Update action) {
        ChatMemberUpdated membershipUpdate = action.getMyChatMember();
        if (membershipUpdate == null) {
            return false;
        }

        return membershipUpdate.getNewChatMember().getStatus().equals("member");
    }
}
