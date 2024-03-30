package me.nasukhov.bot;

import me.nasukhov.db.Collection;
import me.nasukhov.db.Connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChannelRepository {
    private final Connection db;

    public ChannelRepository(Connection db) {
        this.db = db;
    }

    public List<Channel> list() {
        Collection queryResult = db.fetchByQuery("SELECT id FROM channels WHERE is_public=true");

        List<Channel> channels = new ArrayList<>();
        while (queryResult.next()) {
            String channelId = queryResult.getCurrentEntryProp("id");
            channels.add(new Channel(channelId, true));
        }

        return channels;
    }

    public void addChannel(Channel channel) {
        db.executeQuery(
                "INSERT INTO channels(id, is_public, added_at) VALUES(?, ?, CURRENT_TIMESTAMP) ON CONFLICT (id) DO NOTHING",
                new HashMap<>() {{
                    put(1, channel.id);
                    put(2, channel.isPublic());
                }}
        );
    }
}
