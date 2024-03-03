package me.nasukhov.dictionary;

public record Word(String word, String translation, String description, PartOfSpeech pos, String context) {
}
