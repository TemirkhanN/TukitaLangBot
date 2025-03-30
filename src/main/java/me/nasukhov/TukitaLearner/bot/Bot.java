package me.nasukhov.TukitaLearner.bot;

import me.nasukhov.TukitaLearner.bot.command.Handler;
import me.nasukhov.TukitaLearner.bot.io.Channel;
import me.nasukhov.TukitaLearner.bot.io.ChannelRepository;
import me.nasukhov.TukitaLearner.bot.io.Input;
import me.nasukhov.TukitaLearner.bot.io.Output;
import me.nasukhov.TukitaLearner.bot.task.TaskManager;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Bot {
    private final List<Handler> handlers = new ArrayList<>();
    private final ChannelRepository channelRepository;
    private final TaskManager taskManager;

    public Bot(ChannelRepository channelRepository, TaskManager taskManager, List<Handler> handlers) {
        this.channelRepository = channelRepository;
        this.taskManager = taskManager;

        this.handlers.addAll(handlers);
    }

    public void addHandler(Handler handler) {
        handlers.add(handler);
    }

    public void runTasks() {
        taskManager.run();
    }

    public String getName() {
        return "TukitaLangBot";
    }

    // TODO remove output and use output resolver
    public void handle(Input command, Output output) {
        Channel channel = command.channel();
        boolean isKnownChannel = channelRepository.findById(channel.id).isPresent();
        if (isKnownChannel) {
            if (!channelRepository.isActive(channel)) {
                return;
            }
        } else {
            // TODO event driven or something. This domain is a bit leaky
            channelRepository.saveChannel(channel);
            taskManager.registerTasks(channel);
        }

        for (Handler handler : handlers) {
            if (handler.supports(command)) {
                handler.handle(command, output);

                return;
            }
        }
    }
}
