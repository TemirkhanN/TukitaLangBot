package me.nasukhov.bot;

import me.nasukhov.bot.command.Handler;
import me.nasukhov.bot.io.ChannelRepository;
import me.nasukhov.bot.io.Input;
import me.nasukhov.bot.io.Output;
import me.nasukhov.bot.task.TaskManager;

import java.util.ArrayList;
import java.util.List;

public class Bot {
    private final List<Handler> handlers = new ArrayList<>();
    private final ChannelRepository channelRepository;

    private final TaskManager taskManager;

    public Bot(ChannelRepository channelRepository, TaskManager taskManager) {
        this.channelRepository = channelRepository;
        this.taskManager = taskManager;
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
        if (!channelRepository.isActive(command.channel())) {
            return;
        }

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
        handler.handle(command, output);
    }
}
