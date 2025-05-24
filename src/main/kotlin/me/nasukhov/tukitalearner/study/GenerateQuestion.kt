package me.nasukhov.tukitalearner.study

import me.nasukhov.tukitalearner.dictionary.DictionaryEntry
import me.nasukhov.tukitalearner.dictionary.DictionaryRepository
import org.springframework.data.domain.PageRequest
import java.util.*

// TODO this class is more relevant to general configuration rather than study plan
class GenerateQuestion(
    private val dictionary: DictionaryRepository,
    private val questionRepository: QuestionRepository,
) {
    companion object {
        private const val MAX_WORDS = 10000

        private const val REPLY_VARIANTS_QUANTITY = 3

        private const val QUESTION_TRANSLATE_FROM_NATIVE = "Как перевести \"%s\"?"
        private const val QUESTION_TRANSLATE_TO_NATIVE = "Как перевести \"%s\"?"
    }

    private val random: Random = Random()

    fun run() {
        val words = dictionary.findWords(0L, PageRequest.of(0, MAX_WORDS))

        generateWithWords(words)
    }

    private fun generateWithWords(words: List<DictionaryEntry>) {
        for (word in words) {
            val fromNativeVariants = ArrayList<String>()
            val toNativeVariants = ArrayList<String>()
            fromNativeVariants.add(word.translation)
            toNativeVariants.add(word.word)
            for (wrongAnswer in getRandomFromList(REPLY_VARIANTS_QUANTITY, words, word)) {
                fromNativeVariants.add(wrongAnswer.translation)
                toNativeVariants.add(wrongAnswer.word)
            }

            fromNativeVariants.shuffle()
            toNativeVariants.shuffle()

            questionRepository.save(
                Question(
                    String.format(QUESTION_TRANSLATE_FROM_NATIVE, word.word),
                    word.translation,
                    fromNativeVariants,
                ),
            )

            questionRepository.save(
                Question(
                    String.format(QUESTION_TRANSLATE_TO_NATIVE, word.translation),
                    word.word,
                    toNativeVariants,
                ),
            )
        }
    }

    private fun getRandomFromList(
        quantity: Int,
        words: List<DictionaryEntry>,
        excludeMeaningsFromWord: DictionaryEntry,
    ): List<DictionaryEntry> {
        val lastEntryPosition = words.size - 1

        val result: MutableList<DictionaryEntry> = ArrayList<DictionaryEntry>()
        do {
            val randomWord = words[random.nextInt(lastEntryPosition)]

            val intersectsWithWord = randomWord.word == excludeMeaningsFromWord.word
            val intersectsWithMeaning = randomWord.translation == excludeMeaningsFromWord.translation

            if (intersectsWithWord || intersectsWithMeaning) {
                continue
            }

            result.add(randomWord)
        } while (result.size != quantity)

        return result
    }
}
