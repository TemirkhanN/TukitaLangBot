package me.nasukhov.bot.bridge;

import me.nasukhov.ServiceLocator;
import me.nasukhov.bot.io.Channel;
import me.nasukhov.bot.io.Output;
import me.nasukhov.bot.bridge.tg.Telegram;
import me.nasukhov.bot.bridge.tg.TelegramOutput;

public class OutputResolver {
    public static Output resolveFor(Channel channel) {
        if (channel.id.startsWith(Telegram.ID_PREFIX)) {
            return new TelegramOutput(
                    Long.parseLong(channel.id.substring(Telegram.ID_PREFIX.length())),
                    ServiceLocator.instance.locate(Telegram.class)
            );
        }

        throw new RuntimeException("Channel interaction is not supported. Probably some mistake in the code.");
    }
}
