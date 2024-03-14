package me.nasukhov;

import me.nasukhov.bot.bridge.Telegram;

public class Main {
    private static final ServiceLocator serviceLocator = new ServiceLocator();

    public static void main(String[] args) {
        serviceLocator.locate(Telegram.class).run();
    }
}