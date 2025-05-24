package me.nasukhov.TukitaLearner.bot.task

interface TaskRunner {
    fun runTask(task: Task)

    fun subscribesFor(): String
}
