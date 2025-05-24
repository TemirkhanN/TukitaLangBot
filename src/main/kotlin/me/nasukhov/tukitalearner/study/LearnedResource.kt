package me.nasukhov.tukitalearner.study

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "learned_resources")
class LearnedResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var id: Long = 0

    @Column(nullable = false, updatable = false)
    private lateinit var groupId: String

    @Column(nullable = false, updatable = false)
    var resourceId: Long = 0
        private set

    @Column(nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private lateinit var resourceType: ResourceType

    @Column(nullable = false)
    private lateinit var learnedAt: LocalDateTime

    // ORM necessity
    private constructor()

    constructor(groupId: String, resourceId: Long, resourceType: ResourceType, learnedAt: LocalDateTime) {
        this.groupId = groupId
        this.resourceId = resourceId
        this.resourceType = resourceType
        this.learnedAt = learnedAt
    }
}
