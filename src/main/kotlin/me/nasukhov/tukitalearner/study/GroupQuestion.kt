package me.nasukhov.tukitalearner.study

import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.util.UUID

@Entity
@Table(name = "group_questions")
class GroupQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // TODO generate uuid 7
    lateinit var id: UUID
        private set

    @ManyToOne
    @JoinColumn(name = "channel_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    lateinit var group: Group

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private lateinit var question: Question

    val text: String
        get() = question.text

    @OneToMany(mappedBy = "question", cascade = [CascadeType.ALL], orphanRemoval = true)
    private val answers: MutableList<Answer> = ArrayList()

    // ORM necessity
    private constructor()

    constructor(group: Group, question: Question) {
        this.group = group
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
        val answer = Answer(selectedAnswer, user, this)

        answers.add(answer)

        return answer
    }

    fun hasAnswerFromUser(user: String): Boolean = answers.stream().anyMatch { answer: Answer -> answer.userId == user }
}
