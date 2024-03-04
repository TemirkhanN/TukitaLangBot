package me.nasukhov;

import me.nasukhov.bot.Bot;
import me.nasukhov.bot.bridge.Telegram;
import me.nasukhov.dictionary.Repository;
import me.nasukhov.dictionary.Word;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        Bot bot = new Bot();
        runForTelegram(bot);

        Repository dictionary = new Repository();
        for (Word word : dictionary.getByTranslation("ДУМАТЬ")) {
            System.out.println(word.description());
        }
    }

    private static void runForTelegram(Bot bot) {
        try {
            String botToken = System.getenv("TG_BOT_TOKEN");
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new Telegram(botToken, bot));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}