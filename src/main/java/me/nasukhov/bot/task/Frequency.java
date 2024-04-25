package me.nasukhov.bot.task;

import java.util.concurrent.TimeUnit;

public record Frequency(long everyX, TimeUnit time) {
}
