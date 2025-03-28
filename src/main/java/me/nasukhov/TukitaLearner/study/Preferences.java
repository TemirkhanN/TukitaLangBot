package me.nasukhov.TukitaLearner.study;

import me.nasukhov.TukitaLearner.db.Connection;

import java.util.HashMap;

public class Preferences {
    private final Group group;
    private final Connection connection;

    Preferences(Group group, Connection connection) {
        this.connection = connection;
        this.group = group;
    }

    public void enableAutoAsker(boolean status) {
        connection.executeQuery("UPDATE tasks SET is_active=? WHERE channel_id=? AND task_name='ask_question'", new HashMap<>() {{
            put(1, status);
            put(2, group.id());
        }});
    }

    public void autoAskEveryXMinutes(int minutes) {
        connection.executeQuery("UPDATE tasks SET frequency=? WHERE channel_id=? AND task_name='ask_question'", new HashMap<>() {{
            put(1, minutes);
            put(2, group.id());
        }});
    }

    public void enableFactSharing(boolean status) {
        connection.executeQuery("UPDATE tasks SET is_active=? WHERE channel_id=? AND task_name='share_fact'", new HashMap<>() {{
            put(1, status);
            put(2, group.id());
        }});
    }

    public void shareFactEveryXMinutes(int minutes) {
        connection.executeQuery("UPDATE tasks SET frequency=? WHERE channel_id=? AND task_name='share_fact'", new HashMap<>() {{
            put(1, minutes);
            put(2, group.id());
        }});
    }
}
