package me.nasukhov.dictionary;

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
        params.put(1, canonizeText(translation));

        ResultSet result = db.fetchByQuery("SELECT * FROM dictionary WHERE translation=?", params);

        List<Word> matches = new ArrayList<>();
        try {
            while (result.next()) {
                matches.add(mapData(result));
            }
            result.close();
        } catch (SQLException e) {
            // TODO normal abstraction
            throw new RuntimeException(e);
        }

        return matches;
    }

    public List<Word> getChunk(int length, int startingFromId) {
        Map<Integer, Object> params = new HashMap<>();
        params.put(1, startingFromId);
        params.put(2, length);

        ResultSet result = db.fetchByQuery("SELECT * FROM dictionary WHERE id>? ORDER BY id ASC LIMIT ?", params);

        List<Word> matches = new ArrayList<>();
        try {
            while (result.next()) {
                matches.add(mapData(result));
            }
            result.close();
        } catch (SQLException e) {
            // TODO normal abstraction
            throw new RuntimeException(e);
        }

        return matches;
    }

    private void loadFromCsv() {
        /*
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("dictionary.csv");

        boolean headline = true;
        Integer index = 0;
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(is))) {
            String[] columns = null;
            while ((columns = csvReader.readNext()) != null) {
                // skipping columns definition row
                if (headline) {
                    headline = false;
                    continue;
                }

                Word word = new Word(canonizeText(columns[3]), canonizeText(columns[1]), columns[13], PartOfSpeech.fromValue(columns[8]), columns[14]);
                words.add(index, word);
                // TODO There are duplicates overlap in both indexes...
                wordIndex.put(word.word(), index);
                translationIndex.put(word.translation(), index);
                ++index;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        */
    }

    private String canonizeText(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    private Word mapData(ResultSet data) {
        try {
            return new Word(
                    data.getInt("id"),
                    data.getString("word"),
                    data.getString("translation"),
                    data.getString("description"),
                    PartOfSpeech.fromValue(data.getString("part_of_speech")),
                    data.getString("context")
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
