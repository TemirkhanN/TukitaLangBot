package me.nasukhov.study;

import me.nasukhov.dictionary.DictionaryRepository;
import me.nasukhov.dictionary.Word;

import java.util.*;

// TODO there are dubious-meaning words in dictionary which may produce incorrect results. Like Гӏунщи
public class GenerateQuestion {
    private static int MAX = 10000;
    private static String QUESTION_TRANSLATE_FROM_NATIVE = "Как перевести \"%s\" на русский?";
    private static String QUESTION_TRANSLATE_TO_NATIVE = "Как перевести \"%s\" на тукитинский?";

    private final DictionaryRepository dictionary;
    private final QuestionRepository questionRepository;

    private final Random random;

    public static void main(String[] args) {
        new GenerateQuestion(new DictionaryRepository(), new QuestionRepository()).run();
    }

    public GenerateQuestion(DictionaryRepository dictionaryRepository, QuestionRepository questionRepository) {
        dictionary = dictionaryRepository;
        this.questionRepository = questionRepository;
        random = new Random();
    }

    public void run() {
        List<Word> words = dictionary.getChunk(MAX, 0);

        for (Word word: words) {
            List<String> fromNativeVariants = new ArrayList<>();
            List<String> toNativeVariants = new ArrayList<>();
            fromNativeVariants.add(word.translation());
            toNativeVariants.add(word.word());
            for (Word wrongAnswer: getRandom(3, words)) {
                fromNativeVariants.add(wrongAnswer.translation());
                toNativeVariants.add(wrongAnswer.word());
            }

            Collections.shuffle(fromNativeVariants);
            Collections.shuffle(toNativeVariants);

            questionRepository.create(
                    String.format(QUESTION_TRANSLATE_FROM_NATIVE, word.word()),
                    word.translation(),
                    fromNativeVariants
            );

            questionRepository.create(
                    String.format(QUESTION_TRANSLATE_TO_NATIVE, word.translation()),
                    word.word(),
                    toNativeVariants
            );
        }
    }

    private List<Word> getRandom(int quantity, List<Word> words) {
        int lastEntryPosition = words.size() - 1;

        List<Word> result = new ArrayList<>();
        while (--quantity > 0) {
            result.add(words.get(random.nextInt(lastEntryPosition)));
        }

        return result;
    }
}
