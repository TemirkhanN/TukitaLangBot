package me.nasukhov.tukitalearner.study

import java.time.LocalTime

object Time {
    // UTC+3(7:00-23:00). Intentionally simplified to avoid working with datetime and zoneId.
    private val startTime: LocalTime? = LocalTime.of(4, 0)
    private val endTime: LocalTime? = LocalTime.of(20, 0)

    @JvmStatic
    val isOffHours: Boolean
        get() {
            val currentTime = LocalTime.now()

            val isLearningHours =
                currentTime.isAfter(startTime) && currentTime.isBefore(endTime)

            return !isLearningHours
        }
}
