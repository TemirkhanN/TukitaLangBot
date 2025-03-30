package me.nasukhov.TukitaLearner.bot.task;

interface TaskRunner{
    void runTask(Task task);

    String subscribesFor();
}
