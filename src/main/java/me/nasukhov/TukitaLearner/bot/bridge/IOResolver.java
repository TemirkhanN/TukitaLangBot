package me.nasukhov.TukitaLearner.bot.bridge;

import me.nasukhov.TukitaLearner.bot.io.Channel;
import me.nasukhov.TukitaLearner.bot.io.Output;
import me.nasukhov.TukitaLearner.bot.bridge.tg.Telegram;
import me.nasukhov.TukitaLearner.bot.bridge.tg.TelegramOutput;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class IOResolver {
    public final static String TG_PREFIX = "tg_";

    private final ApplicationContext serviceLocator;

    public IOResolver(ApplicationContext applicationContext) {
        this.serviceLocator = applicationContext;
    }

    public Output resolveFor(Channel channel) {
        if (isTelegramChannel(channel)) {
            var chatId = Long.parseLong(channel.id.substring(TG_PREFIX.length()));

            return new TelegramOutput(chatId, serviceLocator.getBean(Telegram.class));
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
