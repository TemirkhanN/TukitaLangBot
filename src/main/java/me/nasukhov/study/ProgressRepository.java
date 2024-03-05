package me.nasukhov.study;

import me.nasukhov.bot.Channel;
import me.nasukhov.db.Connection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProgressRepository {
    private final Connection db;

    public ProgressRepository() {
        db = Connection.getInstance();
    }
    public int getLastLearnedWordId(Channel by) {
        Map<Integer, Object> params = new HashMap<>(){{
            put(1, by.id);
        }};

        try {
            ResultSet result = db.fetchByQuery("SELECT word_id FROM learned_words WHERE channel_id=? ORDER BY learned_at DESC LIMIT 1", params);
            int wordId = 0;
            if (result.next()) {
                wordId = result.getInt("word_id");
            }
            result.close();

            return wordId;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setLastLearnedWords(Channel by, List<Integer> wordIds) {
        for (Integer wordId: wordIds) {
            db.executeQuery(
                    "INSERT INTO learned_words(channel_id, word_id, learned_at) VALUES (?, ?, CURRENT_TIMESTAMP)",
                    new HashMap<>(){{
                        put(1, by.id);
                        put(2, wordId);
                    }}
            );
        }
    }
}
