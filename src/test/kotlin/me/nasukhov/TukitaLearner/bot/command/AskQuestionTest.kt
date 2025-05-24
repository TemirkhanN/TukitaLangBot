package me.nasukhov.TukitaLearner.bot.command

import me.nasukhov.TukitaLearner.bot.io.Channel
import me.nasukhov.TukitaLearner.bot.io.Input
import me.nasukhov.TukitaLearner.bot.io.Output
import me.nasukhov.TukitaLearner.bot.io.User
import me.nasukhov.TukitaLearner.study.GroupQuestion
import me.nasukhov.TukitaLearner.study.GroupQuestionRepository
import me.nasukhov.TukitaLearner.study.Question
import me.nasukhov.TukitaLearner.study.QuestionRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class AskQuestionTest(
    @Autowired private val handler: AskQuestion,

    @Autowired private val groupQuestionRepository: GroupQuestionRepository,

    @Autowired private val questionRepository: QuestionRepository
) {
    private lateinit var output: Output

    companion object {
        private const val TEMPLATE_ASK_QUESTION = "qh answer %s %d"
    }

    @BeforeEach
    fun setup() {
        output = Mockito.mock(Output::class.java)

        questionRepository.save(
            Question(
                "2+5 equals to",
                "seven",
                listOf(
                    "thirteen",
                    "seven",
                    "twenty-one"
                )
            )
        )
    }

    @AfterEach
    fun reset() {
        questionRepository.deleteAll()
    }

    @Test
    fun handleAskWhenAllQuestionsAreAnswered() {
        val channel = Channel("SomeChannelId")
        val user = User("SomeId", "SomeName")
        val input = Input("/ask", channel, user)

        questionRepository.deleteAll()

        handler.handle(input, output)

        Assertions.assertTrue(groupQuestionRepository.findAll().isEmpty())
        Mockito.verify(output).write("У нас пока нет новых вопросов. Проверьте позже")
    }

    @ParameterizedTest
    @ValueSource(strings = ["/ask", "/ask@botName"])
    fun handleAsk(rawInput: String) {
        val channel = Channel("SomeChannelId")
        val user = User("SomeId", "SomeName")
        val input = Input(rawInput, channel, user)

        Assertions.assertTrue(groupQuestionRepository.findAll().isEmpty())

        handler.handle(input, output)

        val allQuestions = groupQuestionRepository.findAll()

        Assertions.assertEquals(1, allQuestions.size, "Expected one question")
        val groupQuestion: GroupQuestion = allQuestions.first()
        Assertions.assertEquals("2+5 equals to", groupQuestion.text)
        Assertions.assertArrayEquals(
            arrayOf("thirteen", "seven", "twenty-one"),
            groupQuestion.listVariants().toTypedArray()
        )

        Mockito.verify(output, Mockito.only())
            .promptChoice(
                "2+5 equals to",
                mapOf(
                    "thirteen" to String.format(TEMPLATE_ASK_QUESTION, groupQuestion.id, 1),
                    "seven" to String.format(TEMPLATE_ASK_QUESTION, groupQuestion.id, 2),
                    "twenty-one" to String.format(TEMPLATE_ASK_QUESTION, groupQuestion.id, 3)
                )
            )
    }
}
