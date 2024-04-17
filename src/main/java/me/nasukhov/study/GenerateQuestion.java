package me.nasukhov.study;

import me.nasukhov.dictionary.DictionaryRepository;
import me.nasukhov.dictionary.Word;

import java.util.*;

public class GenerateQuestion {
    private static final int MAX_WORDS = 10000;

    private static final int REPLY_VARIANTS_QUANTITY = 3;

    private static final String QUESTION_TRANSLATE_FROM_NATIVE = "Как перевести \"%s\"?";
    private static final String QUESTION_TRANSLATE_TO_NATIVE = "Как перевести \"%s\"?";

    private final DictionaryRepository dictionary;
    private final QuestionRepository questionRepository;

    private final Random random;

    public GenerateQuestion(DictionaryRepository dictionaryRepository, QuestionRepository questionRepository) {
        dictionary = dictionaryRepository;
        this.questionRepository = questionRepository;
        random = new Random();
    }

    public void run() {
        List<Word> words = dictionary.getChunk(MAX_WORDS, 0);

        generateWithWords(words);
    }

    private void generateWithWords(List<Word> words) {
        for (Word word : words) {
            List<String> fromNativeVariants = new ArrayList<>();
            List<String> toNativeVariants = new ArrayList<>();
            fromNativeVariants.add(word.translation);
            toNativeVariants.add(word.word);
            for (Word wrongAnswer : getRandomFromList(REPLY_VARIANTS_QUANTITY, words, word)) {
                fromNativeVariants.add(wrongAnswer.translation);
                toNativeVariants.add(wrongAnswer.word);
            }

            Collections.shuffle(fromNativeVariants);
            Collections.shuffle(toNativeVariants);

            questionRepository.create(
                    String.format(QUESTION_TRANSLATE_FROM_NATIVE, word.word),
                    word.translation,
                    fromNativeVariants
            );

            questionRepository.create(
                    String.format(QUESTION_TRANSLATE_TO_NATIVE, word.translation),
                    word.word,
                    toNativeVariants
            );
        }
    }

    private List<Word> getRandomFromList(int quantity, List<Word> words, Word excludeMeaningsFromWord) {
        int lastEntryPosition = words.size() - 1;

        List<Word> result = new ArrayList<>();
        do {
            Word randomWord = words.get(random.nextInt(lastEntryPosition));

            boolean intersectsWithWord = randomWord.word.equals(excludeMeaningsFromWord.word);
            boolean intersectsWithMeaning = randomWord.translation.equals(excludeMeaningsFromWord.translation);

            if (intersectsWithWord || intersectsWithMeaning) {
                continue;
            }

            result.add(randomWord);
        } while (result.size() != quantity);

        return result;
    }
}
