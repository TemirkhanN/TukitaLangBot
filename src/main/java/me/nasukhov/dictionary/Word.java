package me.nasukhov.dictionary;

public class Word{
    public final int id;
    public final String word;
    public final String translation;
    public final String description;
    public final PartOfSpeech pos;
    public final String context;

    public Word(String word, String translation, String description, PartOfSpeech pos, String context) {
        id = 0;
        this.word = word;
        this.translation = translation;
        this.description = description;
        this.pos = pos;
        this.context = context;
    }

    public Word(int id, String word, String translation, String description, PartOfSpeech pos, String context) {
        this.id = id;
        this.word = word;
        this.translation = translation;
        this.description = description;
        this.pos = pos;
        this.context = context;
    }

    public static String canonize(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    }
}
