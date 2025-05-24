package me.nasukhov.TukitaLearner.dictionary

import com.opencsv.CSVReader
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import java.io.InputStreamReader

@Component
@Lazy
class ImportDictionary(private val storage: DictionaryRepository) {
    fun run() {
        val classloader = Thread.currentThread().getContextClassLoader()
        val loader = classloader.getResourceAsStream("dictionary.csv")
        requireNotNull(loader) { "Dictionary file is missing" }

        var headline = true
        val words = mutableListOf<Word>()

        CSVReader(InputStreamReader(loader)).use {
            it.forEach { row ->
                // skipping columns definition row
                if (headline) {
                    headline = false
                    return@forEach
                }

                val word = Word(
                    word = Word.canonize(row[3]),
                    translation = Word.canonize(row[1]),
                    description = row[13],
                    partOfSpeech = PartOfSpeech.fromValue(row[8])
                )
                words.add(word)
            }

            storage.saveAllAndFlush(words)
        }

    }
}
