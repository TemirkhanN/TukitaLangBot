package me.nasukhov;

import me.nasukhov.dictionary.Repository;
import me.nasukhov.dictionary.Word;

public class Main {
    public static void main(String[] args) {
        Repository dictionary = new Repository();

        for (Word word: dictionary.getByTranslation("ДУМАТЬ")) {
            System.out.println(word.description());
        }
    }
}