package me.nasukhov.task

interface TaskRunner {
    fun runTask(task: Task)

    fun subscribesFor(): String
}
