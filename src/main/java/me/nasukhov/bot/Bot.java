package me.nasukhov.bot;

import me.nasukhov.bot.command.Handler;
import me.nasukhov.bot.io.ChannelRepository;
import me.nasukhov.bot.io.Input;
import me.nasukhov.bot.io.Output;
import me.nasukhov.bot.task.Frequency;
import me.nasukhov.bot.task.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Bot {
    private final List<Handler> handlers = new ArrayList<>();
    private final List<Task> tasks = new ArrayList<>();
    private final ChannelRepository channelRepository;

    private boolean isRunning = false;

    public Bot(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    public void addHandler(Handler handler) {
        handlers.add(handler);
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void runTasks() {
        if (isRunning) {
            throw new RuntimeException("Tasks are already running");
        }

        isRunning = true;

        if (tasks.isEmpty()) {
            return;
        }

        // For now, I don't expect many heavy tasks running in parallel. Once it becomes a problem, increase pool
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        for (Task task: tasks) {
            Frequency frequency = task.frequency();
            scheduler.scheduleAtFixedRate(task, 0, frequency.everyX(), frequency.time());
        }
    }

    public String getName() {
        return "TukitaLangBot";
    }

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
