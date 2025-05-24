package me.nasukhov.TukitaLearner.dictionary

enum class PartOfSpeech {
    NOUN,
    PRONOUN,
    VERB,
    ADVERB,
    ADJECTIVE,
    NUMERAL;

    companion object {
        fun fromValue(value: String): PartOfSpeech {
            return when (value) {
                "n" -> NOUN
                "v" -> VERB
                "pron" -> PRONOUN
                "adj" -> ADJECTIVE
                "adv", "other" -> ADVERB
                "num" -> NUMERAL
                else -> valueOf(value)
            }
        }
    }
}
