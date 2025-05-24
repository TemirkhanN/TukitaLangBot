package me.nasukhov.tukitalearner.study

import jakarta.persistence.*

@Entity
@Table(name = "facts")
class Fact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
        private set

    @Column(columnDefinition = "TEXT", nullable = false)
    lateinit var text: String
        private set

    private constructor()

    constructor(text: String) {
        this.text = text
    }
}
