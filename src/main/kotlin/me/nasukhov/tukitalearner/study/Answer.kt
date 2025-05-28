package me.nasukhov.tukitalearner.study

import jakarta.persistence.*

@Entity
@Table(name = "answers")
class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var id: Long = 0

    @Column(nullable = false, updatable = false)
    lateinit var userId: String
        private set

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    lateinit var question: GroupQuestion
        private set

    @Column(nullable = false, updatable = false)
    var isCorrect: Boolean = false
        private set

    // ORM necessity
    private constructor()

    internal constructor(answer: String, byUser: String, toQuestion: GroupQuestion) {
        userId = byUser
        question = toQuestion
        isCorrect = question.viewAnswer() == answer
    }
}
