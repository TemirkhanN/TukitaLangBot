package me.nasukhov.study;

import java.time.LocalTime;

public class Time {

    // UTC+3(7:00-23:00). Intentionally simplified to avoid working with datetime and zoneId.
    private static final LocalTime startTime = LocalTime.of(4, 0);
    private static final LocalTime endTime = LocalTime.of(20, 0);

    public static boolean isOffHours() {
        LocalTime currentTime = LocalTime.now();

        boolean isLearningHours = currentTime.isAfter(startTime) && currentTime.isBefore(endTime);

        return !isLearningHours;
    }
}
