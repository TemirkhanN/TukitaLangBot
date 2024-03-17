package me.nasukhov.dictionary;

import me.nasukhov.db.Collection;
import me.nasukhov.db.Connection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DictionaryRepository {
    private final Connection db;

    public DictionaryRepository() {
        db = Connection.getInstance();
    }

    public List<Word> getByTranslation(String translation) {
        Map<Integer, Object> params = new HashMap<>();
        params.put(1, Word.canonize(translation));

        Collection result = db.fetchByQuery("SELECT * FROM dictionary WHERE translation=?", params);

        List<Word> matches = new ArrayList<>();
        while (result.next()) {
            matches.add(mapData(result));
        }
        result.free();

        return matches;
    }

    public List<Word> getChunk(int length, int startingFromId) {
        Map<Integer, Object> params = new HashMap<>();
        params.put(1, startingFromId);
        params.put(2, length);

        Collection result = db.fetchByQuery("SELECT * FROM dictionary WHERE id>? ORDER BY id ASC LIMIT ?", params);

        List<Word> matches = new ArrayList<>();
        while (result.next()) {
            matches.add(mapData(result));
        }

        result.free();

        return matches;
    }

    public void add(List<Word> words) {
        // This will take a while but it's intentional. I don't want to play around with db layer and batching.
        words.forEach(word -> {
            if (word.id != 0) {
                throw new RuntimeException("Dictionary can add only new entries. Updating existing entries is prohibited");
            }
            Map<Integer, Object> params = new HashMap<>() {{
                put(1, word.word);
                put(2, word.translation);
                put(3, word.pos.toString());
                put(4, word.description);
                put(5, word.context);
            }};
            db.executeQuery("INSERT INTO dictionary(word, translation, part_of_speech, description, context) VALUES(?,?,?,?,?)", params);
        });
    }

    private Word mapData(Collection data) {
        return new Word(
                data.getCurrentEntryProp("id"),
                data.getCurrentEntryProp("word"),
                data.getCurrentEntryProp("translation"),
                data.getCurrentEntryProp("description"),
                PartOfSpeech.fromValue(data.getCurrentEntryProp("part_of_speech")),
                data.getCurrentEntryProp("context")
        );
    }
}
