package me.nasukhov.TukitaLearner.dictionary;

public enum PartOfSpeech {
    NOUN,
    PRONOUN,
    VERB,
    ADVERB,
    ADJECTIVE,
    NUMERAL;

    public static PartOfSpeech fromValue(String value) {
        return switch (value) {
            case "n" -> PartOfSpeech.NOUN;
            case "v" -> PartOfSpeech.VERB;
            case "pron" -> PartOfSpeech.PRONOUN;
            case "adj" -> PartOfSpeech.ADJECTIVE;
            // For some reason YES/NO are considered as OTHER in data, so, we cast it to ADVERB
            case "adv", "other" -> PartOfSpeech.ADVERB;
            case "num" -> PartOfSpeech.NUMERAL;
            default -> PartOfSpeech.valueOf(value);
        };
    }
}
