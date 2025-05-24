package me.nasukhov.TukitaLearner.study

import jakarta.persistence.EntityManager

// TODO rethink this entity. Could be that task service/repository makes more sense
class Preferences internal constructor(private val group: Group, private val connection: EntityManager) {
    fun enableAutoAsker(status: Boolean) {
        connection
            .createNativeQuery("UPDATE tasks SET is_active = :status WHERE channel_id = :channelId AND task_name='ask_question'")
            .setParameter("channelId", group.id)
            .setParameter("status", status)
            .executeUpdate()
    }

    fun autoAskEveryXMinutes(minutes: Int) {
        connection
            .createNativeQuery("UPDATE tasks SET frequency = :frequency WHERE channel_id = :channelId AND task_name='ask_question'")
            .setParameter("channelId", group.id)
            .setParameter("frequency", minutes)
    }

    fun enableFactSharing(status: Boolean) {
        connection
            .createNativeQuery("UPDATE tasks SET is_active = :isActive WHERE channel_id = :channelId AND task_name='share_fact'")
            .setParameter("isActive", status)
            .setParameter("channelId", group.id)
    }

    fun shareFactEveryXMinutes(minutes: Int) {
        connection.createNativeQuery("UPDATE tasks SET frequency = :frequency WHERE channel_id=? AND task_name='share_fact'")
            .setParameter("frequency", minutes)
            .setParameter("channelId", group.id)
    }
}
