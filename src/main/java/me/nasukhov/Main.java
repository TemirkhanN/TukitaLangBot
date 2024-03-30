package me.nasukhov;

import me.nasukhov.bot.bridge.tg.Telegram;

public class Main {
    private static final ServiceLocator serviceLocator = new ServiceLocator();

    public static void main(String[] args) {
        try {
            serviceLocator.locate(Telegram.class).run();
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}