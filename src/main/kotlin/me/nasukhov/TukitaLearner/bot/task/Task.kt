package me.nasukhov.TukitaLearner.bot.task

import jakarta.persistence.*
import me.nasukhov.TukitaLearner.bot.io.Channel
import java.time.LocalDateTime

@Entity
@Table(name = "tasks")
class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
        private set

    @Column(nullable = false, name = "task_name", updatable = false)
    lateinit var name: String
        private set

    @ManyToOne
    lateinit var channel: Channel
        private set

    @Column(nullable = false, name = "frequency")
    var frequencyInMinutes: Int = 0
        private set

    @Column(nullable = false)
    var isActive: Boolean = true
        private set

    @Column
    private var lastExecutedAt: LocalDateTime? = null

    @Column(nullable = false)
    private var nextExecutionAt: LocalDateTime = LocalDateTime.now()

    // ORM necessity
    protected constructor()

    constructor(name: String, frequencyInMinutes: Int, channel: Channel) {
        this.name = name
        this.frequencyInMinutes = frequencyInMinutes
        this.channel = channel
        nextExecutionAt = LocalDateTime.now().plusMinutes(frequencyInMinutes.toLong())
    }

    fun setLastExecutedAt(time: LocalDateTime) {
        lastExecutedAt = time
        nextExecutionAt = nextExecutionAt.plusMinutes(frequencyInMinutes.toLong())
    }
}
