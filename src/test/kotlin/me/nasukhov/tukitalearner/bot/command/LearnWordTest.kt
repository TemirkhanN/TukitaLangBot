package me.nasukhov.tukitalearner.bot.command

import me.nasukhov.tukitalearner.bot.io.Channel
import me.nasukhov.tukitalearner.bot.io.Input
import me.nasukhov.tukitalearner.bot.io.Output
import me.nasukhov.tukitalearner.bot.io.User
import me.nasukhov.tukitalearner.dictionary.DictionaryEntry
import me.nasukhov.tukitalearner.dictionary.DictionaryRepository
import me.nasukhov.tukitalearner.study.Group
import me.nasukhov.tukitalearner.study.LearnedResourceRepository
import me.nasukhov.tukitalearner.study.ProgressTracker
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

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

    private lateinit var output: Output

    @BeforeEach
    fun setup() {
        output = Mockito.mock(Output::class.java)

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
        val channel = Channel("")
        val user = User("SomeId", "SomeName")
        val input = Input("learn", channel, user)

        handler.handle(input, output)

        Mockito.verifyNoInteractions(output)
    }

    @Test
    fun handleWhenAllWordsAreLearned() {
        val channel = Channel("")
        val user = User("SomeId", "SomeName")
        val input = Input("/learn", channel, user)

        progressTracker.setLastLearnedWords(Group(channel.id), dictionary.findAll())

        handler.handle(input, output)

        Mockito.verify(output).write("Вы изучили все слова из нашего словаря - больше новых слов нет.")
        Mockito.verifyNoMoreInteractions(output)
    }

    @Test
    fun handleNormally() {
        val channel = Channel("")
        val user = User("SomeId", "SomeName")
        val input = Input("/learn", channel, user)

        handler.handle(input, output)

        Mockito.verify(output).write(
            """
            гьой - собака
            
            кету - кошка
            
            рухья - дерево
            """.trimIndent(),
        )
    }
}
