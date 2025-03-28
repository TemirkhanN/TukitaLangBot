package me.nasukhov.TukitaLearner.dictionary;

import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ImportDictionary {
    private final DictionaryRepository storage;

    public ImportDictionary(DictionaryRepository dictionaryRepository) {
        storage = dictionaryRepository;
    }

    public void run() {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("dictionary.csv");
        if (is == null) {
            throw new RuntimeException("Could not load dictionary resource");
        }

        boolean headline = true;
        List<Word> words = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(is))) {
            String[] columns = null;
            while ((columns = csvReader.readNext()) != null) {
                // skipping columns definition row
                if (headline) {
                    headline = false;
                    continue;
                }

                Word word = new Word(Word.canonize(columns[3]), Word.canonize(columns[1]), columns[13], PartOfSpeech.fromValue(columns[8]));
                words.add(word);
            }

            storage.add(words);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
