package me.nasukhov.bot.task;

import me.nasukhov.bot.io.Channel;

record Task(int id, String name, Channel channel, int frequency, long lastExecutedAt) {}