package me.nasukhov.TukitaLearner.dictionary;

import jakarta.persistence.*;

@Entity
@Table(name = "dictionary")
public class Word{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public String word;

    @Column(nullable = false)
    public String translation;

    @Column(nullable = false)
    public String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public PartOfSpeech partOfSpeech;

    protected Word() {}

    public Word(String word, String translation, String description) {
        this.word = word;
        this.translation = translation;
        this.description = description;
        this.partOfSpeech = PartOfSpeech.NOUN;
    }

    public Word(String word, String translation, String description, PartOfSpeech partOfSpeech) {
        this.word = word;
        this.translation = translation;
        this.description = description;
        this.partOfSpeech = partOfSpeech;
    }

    public static String canonize(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    }
}
