package me.nasukhov.TukitaLearner.bot.command

import me.nasukhov.TukitaLearner.bot.io.Channel
import me.nasukhov.TukitaLearner.bot.io.Input
import me.nasukhov.TukitaLearner.bot.io.Output
import me.nasukhov.TukitaLearner.bot.io.User
import me.nasukhov.TukitaLearner.study.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
internal class AnswerQuestionTest(
    @Autowired private val handler: AnswerQuestion,

    @Autowired private val progressTracker: ProgressTracker,

    @Autowired private val questionRepository: QuestionRepository
) {
    private lateinit var output: Output

    @BeforeEach
    fun setup() {
        output = Mockito.mock(Output::class.java)

        questionRepository.save(
            Question(
                "2+5 equals to",
                "seven",
                listOf("thirteen", "seven", "twenty-one")
            )
        )
    }

    @AfterEach
    fun reset() {
        questionRepository.deleteAll()
    }

    @Test
    fun handleCorrect() {
        val channel = Channel("SomeChannelId")
        val user = User("SomeId", "SomeName")

        val questionResult = progressTracker.createRandomForChannel(channel)
        Assertions.assertTrue(questionResult.isPresent)
        val newGroupQuestion = questionResult.get()
        Assertions.assertFalse(newGroupQuestion.hasAnswerFromUser(user.name))

        val questionId: String? = newGroupQuestion.id.toString()
        val correctAnswerId = getCorrectVariant(newGroupQuestion)

        val input = Input(String.format("qh answer %s %d", questionId, correctAnswerId), channel, user)

        handler.handle(input, output)

        Mockito
            .verify(output)
            .write("${user.name}, да, правильный ответ «<spoiler>${newGroupQuestion.viewAnswer()}</spoiler>».")

        handler.handle(input, output)

        Mockito.verifyNoMoreInteractions(output)
        // TODO check that answer is recorded exactly once
    }

    private fun getCorrectVariant(groupQuestion: GroupQuestion): Int {
        var variantId = 0

        val correctAnswer = groupQuestion.viewAnswer()
        while (groupQuestion.viewVariant(variantId) != correctAnswer) {
            if (variantId > 1000) {
                throw RuntimeException("Looks like an endless loop")
            }

            variantId++
        }

        return variantId
    }
}
