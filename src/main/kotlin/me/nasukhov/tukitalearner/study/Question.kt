package me.nasukhov.tukitalearner.study

import jakarta.persistence.*
import me.nasukhov.utility.db.StringListConverter
import org.springframework.data.annotation.Immutable

@Entity
@Immutable
@Table(name = "questions")
class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var id: Long = 0

    @Column(nullable = false)
    lateinit var text: String
        private set

    @Column(nullable = false)
    lateinit var answer: String
        private set

    @Convert(converter = StringListConverter::class)
    @Column(columnDefinition = "TEXT")
    lateinit var variants: List<String>
        private set

    private constructor()

    constructor(
        text: String,
        answer: String,
        variants: List<String>,
    ) {
        this.text = text
        this.answer = answer
        this.variants = variants
    }
}
