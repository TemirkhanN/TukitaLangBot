package me.nasukhov.tukitalearner.bot.io

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "channels")
class Channel {
    @Id
    var id: String = ""
        private set

    @Column(nullable = false)
    var isPublic: Boolean = true
        private set

    @Column(nullable = false)
    var isActive: Boolean = true
        private set

    @Column(nullable = false, updatable = false)
    lateinit var addedAt: LocalDateTime
        private set

    // JPA requires a no-arg constructor
    private constructor()

    constructor(id: String) {
        this.id = id
        this.addedAt = LocalDateTime.now()
    }

    constructor(id: String, isPublic: Boolean) {
        this.id = id
        this.isPublic = isPublic
        this.addedAt = LocalDateTime.now()
    }

    fun deactivate() {
        isActive = false
    }

    fun activate() {
        isActive = true
    }
}
