package me.nasukhov.tukitalearner.bot

import me.nasukhov.bot.command.Handler
import me.nasukhov.bot.io.Input
import me.nasukhov.bot.io.NoOutput
import me.nasukhov.bot.io.Output
import me.nasukhov.bot.io.TextOutput
import me.nasukhov.tukitalearner.dictionary.DictionaryRepository
import me.nasukhov.tukitalearner.study.Group
import me.nasukhov.tukitalearner.study.ProgressTracker
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class LearnWord(
    private val dictionary: DictionaryRepository,
    private val progressTracker: ProgressTracker,
) : Handler {
    companion object {
        private const val NO_MORE_UNLEARNED_WORDS = "Вы изучили все слова из нашего словаря - больше новых слов нет."
    }

    override fun supports(input: Input): Boolean = input.isDirectCommand("learn")

    @Transactional
    override fun handle(input: Input): Output {
        if (!supports(input)) {
            return NoOutput()
        }

        val group = Group(input.channel.id)
        val lastLearnedWord = progressTracker.getLastLearnedWordId(group)

        val sb = StringBuilder()
        val newWords = dictionary.findWords(lastLearnedWord, PageRequest.of(0, 3))
        for (word in newWords) {
            sb.append(word.word)
            sb.append(" - ")
            sb.append(word.translation)

            sb.append("\n\n")
        }

        if (sb.isEmpty()) {
            return TextOutput(NO_MORE_UNLEARNED_WORDS)
        }

        progressTracker.setLastLearnedWords(group, newWords)

        // Removing trailing newlines
        sb.delete(sb.length - 2, sb.length)

        return TextOutput(sb.toString())
    }
}
