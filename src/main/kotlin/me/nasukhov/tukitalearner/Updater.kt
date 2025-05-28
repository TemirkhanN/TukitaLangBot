package me.nasukhov.tukitalearner

import me.nasukhov.tukitalearner.dictionary.DictionaryRepository
import me.nasukhov.tukitalearner.dictionary.ImportDictionary
import me.nasukhov.tukitalearner.study.GenerateQuestion
import me.nasukhov.tukitalearner.study.QuestionRepository
import org.springframework.stereotype.Component

@Component
class Updater(
    private val dictionary: DictionaryRepository,
    private val questionRepository: QuestionRepository,
) {
    fun execute() {
        generateData()
    }

    private fun generateData() {
        if (dictionary.count() != 0L) {
            println("Tables already contain generated data. Skipping")

            return
        }
        ImportDictionary(dictionary).run()

        GenerateQuestion(dictionary, questionRepository).run()
    }
}
