package me.nasukhov.tukitalearner.study

import me.nasukhov.tukitalearner.dictionary.DictionaryEntry
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*
import java.util.function.Function

@Component
class ProgressTracker(
    private val learnedResourceRepository: LearnedResourceRepository,
    private val questionRepository: QuestionRepository,
    private val statsRepository: StatsRepository,
    private val groupQuestionRepository: GroupQuestionRepository,
    private val factRepository: FactRepository,
) {
    fun getGroupStats(group: Group): GroupStats {
        val stats = statsRepository.getStatsForGroup(group.id)
        return GroupStats(group, stats)
    }

    fun getLastLearnedWordId(group: Group): Long =
        learnedResourceRepository
            .findLastLearnedWordId(group.id)
            .map(Function { info: LearnedResource -> info.resourceId })
            .orElse(0L)

    fun setLastLearnedWords(
        group: Group,
        words: List<DictionaryEntry>,
    ) {
        val now = LocalDateTime.now()
        val learnedWords =
            words
                .stream()
                .map { word: DictionaryEntry -> LearnedResource(group.id, word.id, ResourceType.WORD, now) }
                .toList()

        learnedResourceRepository.saveAll(learnedWords)
    }

    fun createRandomForGroup(group: Group): Optional<GroupQuestion> {
        val question = questionRepository.findUnseenRandom(group.id)

        if (question.isEmpty) return Optional.empty()

        val groupQuestion = GroupQuestion(group, question.get())
        return Optional.of(groupQuestionRepository.save(groupQuestion))
    }

    fun nextRandomFact(group: Group): Optional<Fact> {
        val fact = factRepository.findUnlearnedFact(group.id)

        fact.ifPresent {
            learnedResourceRepository.save(
                LearnedResource(group.id, it.id, ResourceType.FACT, LocalDateTime.now()),
            )
        }

        return fact
    }
}
