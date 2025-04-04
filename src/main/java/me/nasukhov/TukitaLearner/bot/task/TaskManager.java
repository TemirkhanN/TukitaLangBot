package me.nasukhov.TukitaLearner.bot.task;

import me.nasukhov.TukitaLearner.bot.io.Channel;
import me.nasukhov.TukitaLearner.db.Collection;
import me.nasukhov.TukitaLearner.db.Connection;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
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

    private final Connection db;

    private final ScheduledExecutorService scheduler;

    public TaskManager(Connection db, List<TaskRunner> taskRunners) {
        this.db = db;
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
            for (Task task : getCurrentTasks()) {
                // Just push tasks into the pool and allow them to execute
                scheduler.execute(() -> {
                    getTaskRunner(task).runTask(task);
                    db.executeQuery("UPDATE tasks SET last_executed_at=?, next_execution_at=? WHERE id=?", new HashMap<>(){{
                        put(1, new Timestamp(System.currentTimeMillis()));
                        put(2, new Timestamp(System.currentTimeMillis() + (task.frequency() * 1000L)));
                        put(3, task.id());
                    }});
                });
            }
        };

        scheduler.scheduleAtFixedRate(r, 0, 1, TimeUnit.MINUTES);
    }

    public void registerTasks(Channel channel) {
        db.executeQuery("""
                INSERT INTO tasks(channel_id, task_name, frequency, last_executed_at, next_execution_at) VALUES
                    (?, 'share_fact', 86400, NOW(), NOW()),
                    (?, 'ask_question', 10800, NOW(), NOW())
                """,
                new HashMap<>(){{
                    put(1, channel.id);
                    put(2, channel.id);
                }}
        );
    }

    private List<Task> getCurrentTasks() {
        Collection result = db.fetchByQuery("SELECT t.*, c.is_public" +
                " FROM tasks t " +
                " INNER JOIN channels c ON c.id=t.channel_id AND c.is_active=true" +
                " WHERE t.next_execution_at<=NOW() AND t.is_active=true");

        List<Task> plan = new ArrayList<>();
        while (result.next()) {
            int id = result.getCurrentEntryProp("id");
            String taskName = result.getCurrentEntryProp("task_name");
            String channelId = result.getCurrentEntryProp("channel_id");
            boolean isPublic = result.getCurrentEntryProp("is_public");
            int frequency = result.getCurrentEntryProp("frequency");
            Timestamp lastExecutedAt = result.getCurrentEntryProp("last_executed_at");

            plan.add(new Task(id, taskName, new Channel(channelId, isPublic), frequency, lastExecutedAt.getTime()));
        }
        result.free();

        return plan;
    }

    private TaskRunner getTaskRunner(Task task) {
        if (!runners.containsKey(task.name())) {
            throw new RuntimeException("There is no runner registered for task");
        }

        return runners.get(task.name());
    }
}
