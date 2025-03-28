package me.nasukhov.TukitaLearner.bot.task;

public interface TaskRunner{
    void runTask(Task task);

    String subscribesFor();
}
