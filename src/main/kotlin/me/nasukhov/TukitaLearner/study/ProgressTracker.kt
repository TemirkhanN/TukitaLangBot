package me.nasukhov.TukitaLearner.study

import me.nasukhov.TukitaLearner.bot.io.Channel
import me.nasukhov.TukitaLearner.dictionary.Word
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
    private val factRepository: FactRepository
) {
    fun getGroupStats(group: Group): GroupStats {
        val stats = statsRepository.getStatsForGroup(group.id)
        return GroupStats(group, stats)
    }

    fun getLastLearnedWordId(group: Group): Long {
        return learnedResourceRepository.findLastLearnedWordId(group.id)
            .map(Function { info: LearnedResource -> info.resourceId })
            .orElse(0L)
    }

    fun setLastLearnedWords(group: Group, words: List<Word>) {
        val now = LocalDateTime.now()
        val learnedWords = words.stream()
            .map { word: Word -> LearnedResource(group.id, word.id, ResourceType.WORD, now) }
            .toList()

        learnedResourceRepository.saveAll(learnedWords)
    }

    fun createRandomForChannel(channel: Channel): Optional<GroupQuestion> {
        val question = questionRepository.findUnseenRandom(channel.id)

        if (question.isEmpty) return Optional.empty()

        val groupQuestion = GroupQuestion(channel, question.get())
        return Optional.of(groupQuestionRepository.save<GroupQuestion>(groupQuestion))
    }

    fun findGroupQuestionById(groupQuestionId: UUID): Optional<GroupQuestion> {
        return groupQuestionRepository.findByIdWithAnswers(groupQuestionId)
    }

    fun nextRandomFact(group: Group): Optional<Fact> {
        val fact = factRepository.findUnlearnedFact(group.id)

        fact.ifPresent {
            learnedResourceRepository.save(
                LearnedResource(group.id, it.id, ResourceType.FACT, LocalDateTime.now())
            )
        }

        return fact
    }
}
