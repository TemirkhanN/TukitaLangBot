package me.nasukhov.TukitaLearner.bot.task

import me.nasukhov.TukitaLearner.bot.io.Channel
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@Component
class TaskManager(
    private val taskRepository: TaskRepository,
    taskRunners: List<TaskRunner>
) {
    private var isRunning = false
    private val runners: MutableMap<String, TaskRunner> = ConcurrentHashMap()
    private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(3)

    init {
        taskRunners.forEach(this::registerRunner)
    }

    fun registerRunner(runner: TaskRunner) {
        check(!runners.contains(runner.subscribesFor())) { "Several runners for a single task type" }

        runners[runner.subscribesFor()] = runner
    }

    fun run() {
        check(!isRunning) { "Tasks are already running" }
        isRunning = true

        if (runners.isEmpty()) return

        // TODO check coroutines
        val taskDispatcher = Runnable {
            for (task in taskRepository.getScheduledTasks(PageRequest.of(0, 100))) {
                scheduler.execute {
                    getTaskRunner(task).runTask(task)
                    task.setLastExecutedAt(LocalDateTime.now())
                    taskRepository.save(task)
                }
            }
        }

        scheduler.scheduleAtFixedRate(taskDispatcher, 0, 1, TimeUnit.MINUTES)
    }

    fun registerTasks(channel: Channel) {
        val everyHour = 60
        val TASK_ASK_QUESTION = "ask_question"
        val TASK_SHARE_FACT = "share_fact"
        taskRepository.saveAll(
            listOf(
                Task(TASK_SHARE_FACT, everyHour * 24, channel),
                Task(TASK_ASK_QUESTION, everyHour * 6, channel)
            )
        )
    }

    private fun getTaskRunner(task: Task): TaskRunner {
        return runners[task.name] ?: throw RuntimeException("There is no runner registered for task")
    }
}
