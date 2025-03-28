package me.nasukhov.TukitaLearner.dictionary;

import me.nasukhov.TukitaLearner.db.Collection;
import me.nasukhov.TukitaLearner.db.Connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DictionaryRepository {
    private final Connection db;

    public DictionaryRepository(Connection db) {
        this.db = db;
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
            }};
            db.executeQuery("INSERT INTO dictionary(word, translation, part_of_speech, description) VALUES(?,?,?,?)", params);
        });
    }

    public boolean isEmpty() {
        boolean isEmpty = true;
        Collection result = db.fetchByQuery("SELECT COUNT(*) as total FROM dictionary");
        if (result.next()) {
            isEmpty = (Long) result.getCurrentEntryProp("total") == 0;
        }
        result.free();

        return isEmpty;
    }

    private Word mapData(Collection data) {
        return new Word(
                data.getCurrentEntryProp("id"),
                data.getCurrentEntryProp("word"),
                data.getCurrentEntryProp("translation"),
                data.getCurrentEntryProp("description"),
                PartOfSpeech.fromValue(data.getCurrentEntryProp("part_of_speech"))
        );
    }
}
