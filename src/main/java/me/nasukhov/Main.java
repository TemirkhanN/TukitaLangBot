package me.nasukhov;

import me.nasukhov.bot.Bot;
import me.nasukhov.bot.bridge.Telegram;
import me.nasukhov.db.Connection;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        bootstrap();

        Bot bot = new Bot();
        runForTelegram(bot);
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

    private static void bootstrap() {
        Connection db = Connection.getInstance();
        db.executeQuery(getResourceContent("migrations/initial_schema.sql"));
    }

    private static String getResourceContent(String resource) {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
        if (inputStream == null) {
            throw new RuntimeException("Resource not found");
        }

        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        return bufferedReader.lines().collect(Collectors.joining("\n"));
    }
}