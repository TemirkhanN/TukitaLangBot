package me.nasukhov.dictionary;

public record Word(int id, String word, String translation, String description, PartOfSpeech pos, String context) {
}
