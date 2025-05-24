package me.nasukhov.tukitalearner.dictionary

import jakarta.persistence.*

@Entity
@Table(name = "dictionary")
class DictionaryEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
        private set

    @Column(nullable = false)
    lateinit var word: String
        private set

    @Column(nullable = false)
    lateinit var translation: String
        private set

    @Column(nullable = false)
    lateinit var description: String
        private set

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var partOfSpeech: PartOfSpeech = PartOfSpeech.NOUN
        private set

    // ORM necessity
    private constructor()

    constructor(
        word: String,
        translation: String,
        description: String,
        partOfSpeech: PartOfSpeech = PartOfSpeech.NOUN,
    ) {
        this.word = word
        this.translation = translation
        this.description = description
        this.partOfSpeech = partOfSpeech
    }

    companion object {
        fun canonize(word: String): String = word.substring(0, 1).uppercase() + word.substring(1).lowercase()
    }
}
