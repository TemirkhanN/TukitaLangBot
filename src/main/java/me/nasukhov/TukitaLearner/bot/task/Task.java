package me.nasukhov.TukitaLearner.bot.task;

import me.nasukhov.TukitaLearner.bot.io.Channel;

record Task(int id, String name, Channel channel, int frequency, long lastExecutedAt) {}