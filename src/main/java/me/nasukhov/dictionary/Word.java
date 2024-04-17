package me.nasukhov.dictionary;

public class Word{
    public final int id;
    public final String word;
    public final String translation;
    public final String description;
    public final PartOfSpeech pos;

    public Word(String word, String translation, String description, PartOfSpeech pos) {
        id = 0;
        this.word = word;
        this.translation = translation;
        this.description = description;
        this.pos = pos;
    }

    public Word(int id, String word, String translation, String description, PartOfSpeech pos) {
        this.id = id;
        this.word = word;
        this.translation = translation;
        this.description = description;
        this.pos = pos;
    }

    public static String canonize(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    }
}
