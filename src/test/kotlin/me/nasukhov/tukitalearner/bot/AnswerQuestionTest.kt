package me.nasukhov.tukitalearner.bot

import me.nasukhov.bot.io.Channel
import me.nasukhov.bot.io.Input
import me.nasukhov.bot.io.NoOutput
import me.nasukhov.bot.io.TextOutput
import me.nasukhov.bot.io.User
import me.nasukhov.tukitalearner.study.Group
import me.nasukhov.tukitalearner.study.GroupQuestion
import me.nasukhov.tukitalearner.study.GroupRepository
import me.nasukhov.tukitalearner.study.ProgressTracker
import me.nasukhov.tukitalearner.study.Question
import me.nasukhov.tukitalearner.study.QuestionRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertInstanceOf
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals

@SpringBootTest
@Transactional
internal class AnswerQuestionTest(
    @Autowired private val handler: AnswerQuestion,
    @Autowired private val progressTracker: ProgressTracker,
    @Autowired private val questionRepository: QuestionRepository,
    @Autowired private val groupRepository: GroupRepository,
) {
    private lateinit var group: Group

    @BeforeEach
    fun setup() {
        group = Group("SomeGroup123")
        groupRepository.save(group)
        questionRepository.save(
            Question(
                "2+5 equals to",
                "seven",
                listOf("thirteen", "seven", "twenty-one"),
            ),
        )
    }

    @AfterEach
    fun reset() {
        questionRepository.deleteAll()
        // groupRepository.delete(group)
    }

    @Test
    fun handleCorrect() {
        val channel = Channel(group.id)
        val user = User("SomeId", "SomeName")

        val questionResult = progressTracker.createRandomForGroup(group)
        Assertions.assertTrue(questionResult.isPresent)
        val newGroupQuestion = questionResult.get()
        Assertions.assertFalse(newGroupQuestion.hasAnswerFromUser(user.name))

        val questionId: String? = newGroupQuestion.id.toString()
        val correctAnswerId = getCorrectVariant(newGroupQuestion)

        val input = Input("qh answer $questionId $correctAnswerId", channel, user)

        val output = handler.handle(input)
        assertInstanceOf<TextOutput>(output)
        assertEquals(
            "${user.name}, да, правильный ответ «<spoiler>${newGroupQuestion.viewAnswer()}</spoiler>».",
            output.value,
        )

        assertInstanceOf<NoOutput>(handler.handle(input))
        // TODO check that answer is recorded exactly once
    }

    private fun getCorrectVariant(groupQuestion: GroupQuestion): Int {
        var variantId = 0

        val correctAnswer = groupQuestion.viewAnswer()
        while (groupQuestion.viewVariant(variantId) != correctAnswer) {
            check(variantId++ < 1000) { "Looks like an endless loop" }
        }

        return variantId
    }
}
