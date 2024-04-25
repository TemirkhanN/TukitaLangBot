package me.nasukhov.bot.task;

public interface Task extends Runnable{
    Frequency frequency();
}
