package me.nasukhov.bot.bridge;

import me.nasukhov.DI.ServiceLocator;
import me.nasukhov.bot.io.Channel;
import me.nasukhov.bot.io.Output;
import me.nasukhov.bot.bridge.tg.Telegram;
import me.nasukhov.bot.bridge.tg.TelegramOutput;

public class IOResolver {
    public final static String TG_PREFIX = "tg_";

    public static Output resolveFor(Channel channel) {
        if (isTelegramChannel(channel)) {
            return new TelegramOutput(
                    Long.parseLong(channel.id.substring(TG_PREFIX.length())),
                    ServiceLocator.instance.locate(Telegram.class)
            );
        }

        throw new RuntimeException("Channel interaction is not supported. Probably some mistake in the code.");
    }

    public static Channel telegramChannel(Long chatId, boolean isPublic) {
        return new Channel(TG_PREFIX + chatId, isPublic);
    }

    private static boolean isTelegramChannel(Channel channel) {
        return channel.id.startsWith(TG_PREFIX);
    }
}
