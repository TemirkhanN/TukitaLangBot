package me.nasukhov.tukitalearner.dictionary

enum class PartOfSpeech {
    NOUN,
    PRONOUN,
    VERB,
    ADVERB,
    ADJECTIVE,
    NUMERAL,
    ;

    companion object {
        fun fromValue(value: String): PartOfSpeech =
            when (value) {
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
