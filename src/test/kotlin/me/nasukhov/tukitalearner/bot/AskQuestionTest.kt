package me.nasukhov.tukitalearner.bot

import me.nasukhov.bot.io.Channel
import me.nasukhov.bot.io.Input
import me.nasukhov.bot.io.PromptOutput
import me.nasukhov.bot.io.TextOutput
import me.nasukhov.bot.io.User
import me.nasukhov.tukitalearner.study.Group
import me.nasukhov.tukitalearner.study.GroupQuestionRepository
import me.nasukhov.tukitalearner.study.GroupRepository
import me.nasukhov.tukitalearner.study.Question
import me.nasukhov.tukitalearner.study.QuestionRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertInstanceOf
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest
@Transactional
class AskQuestionTest(
    @Autowired private val handler: AskQuestion,
    @Autowired private val groupQuestionRepository: GroupQuestionRepository,
    @Autowired private val questionRepository: QuestionRepository,
    @Autowired private val groupRepository: GroupRepository,
) {
    private lateinit var group: Group

    companion object {
        private const val TEMPLATE_ASK_QUESTION = "qh answer %s %d"
    }

    @BeforeEach
    fun setup() {
        group = Group("SomeGroup123")
        groupRepository.save(group)
        questionRepository.save(
            Question(
                "2+5 equals to",
                "seven",
                listOf(
                    "thirteen",
                    "seven",
                    "twenty-one",
                ),
            ),
        )
    }

    @AfterEach
    fun reset() {
        questionRepository.deleteAll()
    }

    @Test
    fun handleAskWhenAllQuestionsAreAnswered() {
        val channel = Channel(group.id)
        val user = User("SomeId", "SomeName")
        val input = Input("/ask", channel, user)

        questionRepository.deleteAll()

        val output = handler.handle(input)
        assertInstanceOf<TextOutput>(output)
        assertEquals("У нас пока нет новых вопросов. Проверьте позже", output.value)

        assertTrue(groupQuestionRepository.findAll().isEmpty())
    }

    @ParameterizedTest
    @ValueSource(strings = ["/ask", "/ask@botName"])
    fun handleAsk(rawInput: String) {
        val channel = Channel(group.id)
        val user = User("SomeId", "SomeName")
        val input = Input(rawInput, channel, user)

        assertTrue(groupQuestionRepository.findAll().isEmpty())

        val output = handler.handle(input)
        val allQuestions = groupQuestionRepository.findAll()
        val groupQuestion = allQuestions.first()

        assertInstanceOf<PromptOutput>(output)
        assertEquals(
            PromptOutput(
                "2+5 equals to",
                mapOf(
                    "thirteen" to String.format(TEMPLATE_ASK_QUESTION, groupQuestion.id, 1),
                    "seven" to String.format(TEMPLATE_ASK_QUESTION, groupQuestion.id, 2),
                    "twenty-one" to String.format(TEMPLATE_ASK_QUESTION, groupQuestion.id, 3),
                ),
            ),
            output,
        )

        assertEquals(1, allQuestions.size, "Expected one question")
        assertEquals("2+5 equals to", groupQuestion.text)
        Assertions.assertArrayEquals(
            arrayOf("thirteen", "seven", "twenty-one"),
            groupQuestion.listVariants().toTypedArray(),
        )
    }
}
