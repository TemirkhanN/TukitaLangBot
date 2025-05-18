package me.nasukhov.TukitaLearner.bot.task;

import me.nasukhov.TukitaLearner.bot.io.Channel;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class TaskManager {
    private boolean isRunning = false;

    private final Map<String, TaskRunner> runners = new ConcurrentHashMap<>();

    private final TaskRepository taskRepository;

    private final ScheduledExecutorService scheduler;

    public TaskManager(TaskRepository taskRepository, List<TaskRunner> taskRunners) {
        this.taskRepository = taskRepository;
        this.scheduler = Executors.newScheduledThreadPool(3);
        taskRunners.forEach(this::registerRunner);
    }

    public void registerRunner(TaskRunner runner) {
        runners.put(runner.subscribesFor(), runner);
    }

    public void run() {
        if (isRunning) {
            throw new RuntimeException("Tasks are already running");
        }
        isRunning = true;

        if (runners.isEmpty()) {
            return;
        }

        Runnable r = () -> {
            for (Task task : taskRepository.getScheduledTasks(PageRequest.of(0, 100))) {
                // Just push tasks into the pool and allow them to execute
                // TODO implement lock mechanism to avoid races. if tasks take long to handle, there will be duplicate handles
                scheduler.execute(() -> {
                    getTaskRunner(task).runTask(task);
                    task.setLastExecutedAt(LocalDateTime.now());
                    taskRepository.save(task);
                });
            }
        };

        scheduler.scheduleAtFixedRate(r, 0, 1, TimeUnit.MINUTES);
    }

    public void registerTasks(Channel channel) {
        var everyHour = 60;
        String TASK_ASK_QUESTION = "ask_question";
        String TASK_SHARE_FACT = "share_fact";
        taskRepository.saveAll(
                List.of(
                        new Task(TASK_SHARE_FACT, everyHour * 24, channel),
                        new Task(TASK_ASK_QUESTION, everyHour * 6, channel)
                )
        );
    }

    private TaskRunner getTaskRunner(Task task) {
        if (!runners.containsKey(task.getName())) {
            throw new RuntimeException("There is no runner registered for task");
        }

        return runners.get(task.getName());
    }
}
