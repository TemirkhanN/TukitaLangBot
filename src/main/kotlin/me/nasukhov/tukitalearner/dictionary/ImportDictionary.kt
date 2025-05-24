package me.nasukhov.tukitalearner.dictionary

import com.opencsv.CSVReader
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import java.io.InputStreamReader

@Component
@Lazy
class ImportDictionary(
    private val storage: DictionaryRepository,
) {
    fun run() {
        val classloader = Thread.currentThread().getContextClassLoader()
        val loader = classloader.getResourceAsStream("dictionary.csv")
        requireNotNull(loader) { "Dictionary file is missing" }

        var headline = true
        val dictionaryEntries = mutableListOf<DictionaryEntry>()

        CSVReader(InputStreamReader(loader)).use {
            it.forEach { row ->
                // skipping columns definition row
                if (headline) {
                    headline = false
                    return@forEach
                }

                val dictionaryEntry =
                    DictionaryEntry(
                        word = DictionaryEntry.canonize(row[3]),
                        translation = DictionaryEntry.canonize(row[1]),
                        description = row[13],
                        partOfSpeech = PartOfSpeech.fromValue(row[8]),
                    )
                dictionaryEntries.add(dictionaryEntry)
            }

            storage.saveAllAndFlush(dictionaryEntries)
        }
    }
}
