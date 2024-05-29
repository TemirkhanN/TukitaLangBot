package me.nasukhov.bot.io;

import me.nasukhov.db.Collection;
import me.nasukhov.db.Connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class ChannelRepository {
    private final Connection db;

    public ChannelRepository(Connection db) {
        this.db = db;
    }

    public List<Channel> list() {
        Collection queryResult = db.fetchByQuery("SELECT id FROM channels WHERE is_public=true AND is_active=true");

        List<Channel> channels = new ArrayList<>();
        while (queryResult.next()) {
            String channelId = queryResult.getCurrentEntryProp("id");
            channels.add(new Channel(channelId, true));
        }

        queryResult.free();

        return channels;
    }

    public void saveChannel(Channel channel) {
        db.executeQuery(
                "INSERT INTO channels(id, is_public, added_at) VALUES(?, ?, CURRENT_TIMESTAMP) ON CONFLICT (id) DO NOTHING",
                new HashMap<>() {{
                    put(1, channel.id);
                    put(2, channel.isPublic());
                }}
        );
    }

    public void activateChannel(Channel channel, boolean active) {
        db.executeQuery(
                "UPDATE channels SET is_active=? WHERE id=?",
                new HashMap<>(){{
                    put(1, active);
                    put(2, channel.id);
                }}
        );
    }

    public boolean isActive(Channel channel) {
        Collection queryResult = db.fetchByQuery("SELECT is_active FROM channels WHERE id=?",
                new HashMap<>(){{
                    put(1, channel.id);
                }}
        );

        // If there is no such channel, we consider it as active since there is no direct indication for the state
        if (!queryResult.next()) {
            queryResult.free();

            return true;
        }

        boolean isActive = queryResult.getCurrentEntryProp("is_active");
        queryResult.free();

        return isActive;
    }

    public Optional<Channel> findById(String id) {
        Collection queryResult = db.fetchByQuery("SELECT id, is_public FROM channels WHERE id = ?", new HashMap<>() {{
            put(1, id);
        }});

        if (!queryResult.next()) {
            queryResult.free();

            return Optional.empty();
        }

        String channelId = queryResult.getCurrentEntryProp("id");
        boolean isPublic = queryResult.getCurrentEntryProp("is_public");
        Channel result = new Channel(channelId, isPublic);

        queryResult.free();

        return Optional.of(result);
    }
}
