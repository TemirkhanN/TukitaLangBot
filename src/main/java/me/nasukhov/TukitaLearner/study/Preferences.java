package me.nasukhov.TukitaLearner.study;

import jakarta.persistence.EntityManager;

public class Preferences {
    private final Group group;
    private final EntityManager connection;

    Preferences(Group group, EntityManager connection) {
        this.connection = connection;
        this.group = group;
    }

    public void enableAutoAsker(boolean status) {
        connection
                .createNativeQuery("UPDATE tasks SET is_active = :status WHERE channel_id = :channelId AND task_name='ask_question'")
                .setParameter("channelId", group.id())
                .setParameter("status", status)
                .executeUpdate();
    }

    public void autoAskEveryXMinutes(int minutes) {
        connection
                .createNativeQuery("UPDATE tasks SET frequency = :frequency WHERE channel_id = :channelId AND task_name='ask_question'")
                .setParameter("channelId", group.id())
                .setParameter("frequency", minutes)
        ;
    }

    public void enableFactSharing(boolean status) {
        connection
                .createNativeQuery("UPDATE tasks SET is_active = :isActive WHERE channel_id = :channelId AND task_name='share_fact'")
                .setParameter("isActive", status)
                .setParameter("channelId", group.id());
    }

    public void shareFactEveryXMinutes(int minutes) {
        connection.createNativeQuery("UPDATE tasks SET frequency = :frequency WHERE channel_id=? AND task_name='share_fact'")
                .setParameter("frequency", minutes)
                .setParameter("channelId", group.id());
    }
}
