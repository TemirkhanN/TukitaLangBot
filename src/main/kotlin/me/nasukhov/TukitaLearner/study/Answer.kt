package me.nasukhov.TukitaLearner.study

import jakarta.persistence.*

@Entity
@Table(name = "ch_question_replies")
class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var id: Long = 0

    @Column(nullable = false, updatable = false)
    lateinit var userId: String
        private set

    @Column(nullable = false, updatable = false)
    lateinit var channelId: String
        private set

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    lateinit var question: GroupQuestion
        private set

    @Column(nullable = false, updatable = false)
    var isCorrect: Boolean = false
        private set

    // JPA needs this
    protected constructor()

    constructor(userId: String, channelId: String, question: GroupQuestion, isCorrect: Boolean) {
        this.userId = userId
        this.channelId = channelId
        this.question = question
        this.isCorrect = isCorrect
    }
}
