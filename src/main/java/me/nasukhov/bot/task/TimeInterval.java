package me.nasukhov.bot.task;

import java.util.concurrent.TimeUnit;

public record TimeInterval(long everyX, TimeUnit time) {
}
