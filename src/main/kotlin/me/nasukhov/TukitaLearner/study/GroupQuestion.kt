package me.nasukhov.TukitaLearner.study

import jakarta.persistence.*
import me.nasukhov.TukitaLearner.bot.io.Channel
import java.util.UUID

@Entity
@Table(name = "ch_questions")
class GroupQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // TODO generate uuid 7
    lateinit var id: UUID
        private set

    @Column(nullable = false, updatable = false)
    lateinit var channelId: String
        private set

    @ManyToOne
    private lateinit var question: Question

    val text: String
        get() = question.text

    @OneToMany(mappedBy = "question", cascade = [CascadeType.ALL], orphanRemoval = true)
    private val answers: MutableList<Answer> = ArrayList()

    // ORM necessity
    protected constructor()

    // TODO could id generation encapsulation make it better?
    constructor(channel: Channel, question: Question) {
        this.channelId = channel.id
        this.question = question
    }

    private fun isCorrectAnswer(numberOfAnswer: Int): Boolean {
        val selectedAnswer = viewVariant(numberOfAnswer)
        val correctAnswer = question.answer

        return selectedAnswer == correctAnswer
    }

    fun viewAnswer(): String {
        return question.answer
    }

    fun viewVariant(variant: Int): String {
        if (variant < 1) {
            return ""
        }

        if (variant > question.variants.size) {
            return ""
        }

        return question.variants[variant - 1]
    }

    fun listVariants(): List<String> {
        return question.variants
    }

    fun addAnswer(user: String, selectedAnswer: Int): Answer {
        if (hasAnswerFromUser(user)) {
            throw RuntimeException("User has already answered this question")
        }

        val answer = Answer(
            user,
            channelId,
            this,
            isCorrectAnswer(selectedAnswer)
        )

        answers.add(answer)

        return answer
    }

    fun hasAnswerFromUser(user: String?): Boolean {
        return answers.stream().anyMatch { answer: Answer? -> answer!!.userId == user }
    }
}
