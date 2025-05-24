package me.nasukhov.tukitalearner.bot.task

interface TaskRunner {
    fun runTask(task: Task)

    fun subscribesFor(): String
}
