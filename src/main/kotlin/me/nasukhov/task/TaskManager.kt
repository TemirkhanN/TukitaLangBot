package me.nasukhov.task

import me.nasukhov.bot.io.Channel
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

// @Component
class TaskManager(
    private val taskRepository: TaskRepository,
    taskRunners: List<TaskRunner>,
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
        val taskDispatcher = {
            for (task in taskRepository.getScheduledTasks(PageRequest.of(0, 100))) {
                scheduler.execute {
                    getTaskRunner(task).runTask(task)
                    task.setLastExecutedAt(LocalDateTime.now())
                }
            }
        }

        scheduler.scheduleAtFixedRate(taskDispatcher, 0, 1, TimeUnit.MINUTES)
    }

    fun registerTasks(channel: Channel) {
        val daily = 24 * 60
        val everySixHours = 6 * 60
        val mandatoryTasks =
            mapOf(
                "ask_question" to everySixHours,
                "share_fact" to daily,
            )

        taskRepository.saveAll(
            mandatoryTasks.map { Task(it.key, it.value, channel) },
        )
    }

    private fun getTaskRunner(task: Task): TaskRunner = runners[task.name]!!
}
