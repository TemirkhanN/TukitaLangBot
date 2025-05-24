package me.nasukhov.tukitalearner.study

import jakarta.persistence.*
import me.nasukhov.tukitalearner.bot.io.Channel
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
    private constructor()

    // TODO could id generation encapsulation make it better?
    constructor(channel: Channel, question: Question) {
        this.channelId = channel.id
        this.question = question
    }

    fun viewAnswer(): String = question.answer

    fun viewVariant(variant: Int): String {
        if (variant < 1) {
            return ""
        }

        if (variant > question.variants.size) {
            return ""
        }

        return question.variants[variant - 1]
    }

    fun listVariants(): List<String> = question.variants

    fun addAnswer(
        user: String,
        selectedVariant: Int,
    ): Answer {
        check(!hasAnswerFromUser(user)) {
            "User has already answered this question"
        }

        val selectedAnswer = viewVariant(selectedVariant)
        val answer = Answer(user, channelId, this, selectedAnswer)

        answers.add(answer)

        return answer
    }

    fun hasAnswerFromUser(user: String): Boolean = answers.stream().anyMatch { answer: Answer -> answer.userId == user }
}
