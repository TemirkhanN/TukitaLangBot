package me.nasukhov.bot.task;

public interface TaskRunner{
    void runTask(Task task);

    String subscribesFor();
}
