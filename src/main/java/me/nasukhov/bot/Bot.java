package me.nasukhov.bot;

import me.nasukhov.bot.command.Handler;
import me.nasukhov.bot.io.ChannelRepository;
import me.nasukhov.bot.io.Input;
import me.nasukhov.bot.io.Output;

import java.util.ArrayList;
import java.util.List;

public class Bot {
    private final List<Handler> handlers = new ArrayList<>();
    private final ChannelRepository channelRepository;

    public Bot(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    public void addHandler(Handler handler) {
        handlers.add(handler);
    }

    public String getName() {
        return "TukitaLangBot";
    }

    public void handle(Input command, Output output) {
        for (Handler handler : handlers) {
            if (handler.supports(command)) {
                invokeHandler(command, output, handler);

                return;
            }
        }
    }

    /**
     * Middleware wannabe. Avoiding overkill for now.
     */
    private void invokeHandler(Input command, Output output, Handler handler) {
        channelRepository.addChannel(command.channel());

        if (!channelRepository.isActive(command.channel())) {
            return;
        }

        handler.handle(command, output);
    }
}
