package me.nasukhov.tukitalearner.study

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "groups")
class Group {
    @Id
    lateinit var id: String
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

    fun deactivate() {
        isActive = false
    }

    fun activate() {
        isActive = true
    }

    fun preferences(db: EntityManager): Preferences = Preferences(this, db)
}
