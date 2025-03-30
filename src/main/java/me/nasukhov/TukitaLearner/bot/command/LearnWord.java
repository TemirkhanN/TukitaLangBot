package me.nasukhov.TukitaLearner.bot.command;

import me.nasukhov.TukitaLearner.bot.io.Input;
import me.nasukhov.TukitaLearner.bot.io.Output;
import me.nasukhov.TukitaLearner.study.Group;
import me.nasukhov.TukitaLearner.study.ProgressRepository;
import me.nasukhov.TukitaLearner.dictionary.DictionaryRepository;
import me.nasukhov.TukitaLearner.dictionary.Word;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LearnWord implements Handler {
    private static final String NO_MORE_UNLEARNED_WORDS = "Вы изучили все слова из нашего словаря - больше новых слов нет.";

    private final DictionaryRepository dictionary;
    private final ProgressRepository progressRepository;

    public LearnWord(DictionaryRepository dictionaryRepository, ProgressRepository progressRepository) {
        this.dictionary = dictionaryRepository;
        this.progressRepository = progressRepository;
    }

    @Override
    public boolean supports(Input input) {
        return input.isDirectCommand("learn");
    }

    @Override
    public void handle(Input input, Output output) {
        if (!supports(input)) {
            return;
        }

        Group group = new Group(input.channel().id);
        int lastLearnedWord = progressRepository.getLastLearnedWordId(group);
        List<Integer> newWordsIds = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        for (Word word : dictionary.getChunk(3, lastLearnedWord)) {
            sb.append(word.word);
            sb.append(" - ");
            sb.append(word.translation);

            // TODO show description only for ambiguous words or words with high complexity(indicate in db?)
            //sb.append(word.description);
            sb.append("\n\n");
            newWordsIds.add(word.id);
        }

        if (sb.isEmpty()) {
            output.write(NO_MORE_UNLEARNED_WORDS);

            return;
        }

        progressRepository.setLastLearnedWords(group, newWordsIds);

        // Removing trailing newlines
        sb.delete(sb.length() - 2, sb.length());
        output.write(sb.toString());
    }
}
