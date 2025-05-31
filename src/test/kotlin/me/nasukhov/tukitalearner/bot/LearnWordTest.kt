package me.nasukhov.tukitalearner.bot

import me.nasukhov.bot.io.Channel
import me.nasukhov.bot.io.Input
import me.nasukhov.bot.io.NoOutput
import me.nasukhov.bot.io.TextOutput
import me.nasukhov.bot.io.User
import me.nasukhov.tukitalearner.dictionary.DictionaryEntry
import me.nasukhov.tukitalearner.dictionary.DictionaryRepository
import me.nasukhov.tukitalearner.study.Group
import me.nasukhov.tukitalearner.study.LearnedResourceRepository
import me.nasukhov.tukitalearner.study.ProgressTracker
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertInstanceOf
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals

@SpringBootTest
@Transactional
class LearnWordTest {
    @Autowired
    private lateinit var handler: LearnWord

    @Autowired
    private lateinit var dictionary: DictionaryRepository

    @Autowired
    private lateinit var progressTracker: ProgressTracker

    @Autowired
    private lateinit var learnedResourceRepository: LearnedResourceRepository

    @BeforeEach
    fun setup() {
        dictionary.saveAll(
            listOf(
                DictionaryEntry("гьой", "собака", "Делает кусь"),
                DictionaryEntry("кету", "кошка", "Для глажки"),
                DictionaryEntry("рухья", "дерево", "Вырабатывает кислород ми спасает от жары"),
            ),
        )
    }

    @AfterEach
    fun reset() {
        learnedResourceRepository.deleteAll()
        dictionary.deleteAll()
    }

    @Test
    fun handleUnsupportedInput() {
        val channel = Channel("SomeChannelId")
        val user = User("SomeId", "SomeName")
        val input = Input("learn", channel, user)

        val output = handler.handle(input)
        assertInstanceOf<NoOutput>(output)
    }

    @Test
    fun handleWhenAllWordsAreLearned() {
        val channel = Channel("SomeChannelId")
        val user = User("SomeId", "SomeName")
        val input = Input("/learn", channel, user)

        progressTracker.setLastLearnedWords(Group(channel.id), dictionary.findAll())

        val output = handler.handle(input)
        assertInstanceOf<TextOutput>(output)
        assertEquals("Вы изучили все слова из нашего словаря - больше новых слов нет.", output.value)
    }

    @Test
    fun handleNormally() {
        val channel = Channel("SomeChannelId")
        val user = User("SomeId", "SomeName")
        val input = Input("/learn", channel, user)

        val output = handler.handle(input)
        assertInstanceOf<TextOutput>(output)

        assertEquals(
            """
            гьой - собака

            кету - кошка

            рухья - дерево
            """.trimIndent(),
            output.value,
        )
    }
}
