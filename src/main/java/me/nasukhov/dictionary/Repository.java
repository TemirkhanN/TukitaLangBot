package me.nasukhov.dictionary;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Repository {
    private Connection db;

    public Repository() {
        connect();
    }

    public List<Word> getByTranslation(String translation) {
        List<Word> matches = new ArrayList<>();
        try {
            PreparedStatement stmt = db.prepareStatement("SELECT * FROM dictionary WHERE translation=?");
            stmt.setString(1, canonizeText(translation));

            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                matches.add(
                        new Word(
                                result.getString("word"),
                                result.getString("translation"),
                                result.getString("description"),
                                PartOfSpeech.fromValue(result.getString("part_of_speech")),
                                result.getString("context")
                        )
                );
            }
            stmt.close();
        } catch (SQLException e) {
            return matches;
        }

        return matches;
    }

    private void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            URL url = Thread.currentThread().getContextClassLoader().getResource("app.db");
            if (url == null) {
                throw new Exception("Database file not found in resources directory");
            }
            Path path = Paths.get(url.toURI());
            db = DriverManager.getConnection("jdbc:sqlite:" + path);
        } catch (Exception e) {
            e.printStackTrace();

            throw new RuntimeException("Error occurred while connecting to db");
        }
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
}
