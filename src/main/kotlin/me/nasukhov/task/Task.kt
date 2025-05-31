package me.nasukhov.task

import jakarta.persistence.Column
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import me.nasukhov.bot.io.Channel
import java.time.LocalDateTime

// @Entity
// @Table(name = "tasks")
class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
        private set

    @Column(nullable = false, name = "task_name", updatable = false)
    lateinit var name: String
        private set

    @Column(name = "channel_id", nullable = false, updatable = false)
    private lateinit var channel: String

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
    internal constructor()

    constructor(name: String, frequencyInMinutes: Int, channel: Channel) {
        this.name = name
        this.frequencyInMinutes = frequencyInMinutes
        this.channel = channel.id
        nextExecutionAt = LocalDateTime.now().plusMinutes(frequencyInMinutes.toLong())
    }

    fun setLastExecutedAt(time: LocalDateTime) {
        lastExecutedAt = time
        nextExecutionAt = nextExecutionAt.plusMinutes(frequencyInMinutes.toLong())
    }

    fun getChannel() = Channel(channel)
}
