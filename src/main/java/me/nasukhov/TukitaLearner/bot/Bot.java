package me.nasukhov.TukitaLearner.bot;

import me.nasukhov.TukitaLearner.bot.command.Handler;
import me.nasukhov.TukitaLearner.bot.io.Input;
import me.nasukhov.TukitaLearner.bot.io.Output;
import me.nasukhov.TukitaLearner.bot.task.TaskManager;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Bot {
    private final List<Handler> handlers = new ArrayList<>();
    private final TaskManager taskManager;

    public Bot(TaskManager taskManager, List<Handler> handlers) {
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
        for (Handler handler : handlers) {
            if (handler.supports(command)) {
                handler.handle(command, output);

                return;
            }
        }
    }
}
