package me.nasukhov.TukitaLearner.bot.command;

import me.nasukhov.TukitaLearner.bot.io.Input;
import me.nasukhov.TukitaLearner.bot.io.Output;
import me.nasukhov.TukitaLearner.study.Group;
import me.nasukhov.TukitaLearner.study.ProgressTracker;
import me.nasukhov.TukitaLearner.dictionary.DictionaryRepository;
import me.nasukhov.TukitaLearner.dictionary.Word;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
public class LearnWord implements Handler {
    private static final String NO_MORE_UNLEARNED_WORDS = "Вы изучили все слова из нашего словаря - больше новых слов нет.";

    private final DictionaryRepository dictionary;
    private final ProgressTracker progressTracker;

    public LearnWord(DictionaryRepository dictionaryRepository, ProgressTracker progressTracker) {
        this.dictionary = dictionaryRepository;
        this.progressTracker = progressTracker;
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
        Long lastLearnedWord = progressTracker.getLastLearnedWordId(group);

        StringBuilder sb = new StringBuilder();
        var newWords = dictionary.findWords(lastLearnedWord, PageRequest.of(0, 3));
        for (Word word : newWords) {
            sb.append(word.word);
            sb.append(" - ");
            sb.append(word.translation);

            // TODO show description only for ambiguous words or words with high complexity(indicate in db?)
            //sb.append(word.description);
            sb.append("\n\n");
        }

        if (sb.isEmpty()) {
            output.write(NO_MORE_UNLEARNED_WORDS);

            return;
        }

        progressTracker.setLastLearnedWords(group, newWords);

        // Removing trailing newlines
        sb.delete(sb.length() - 2, sb.length());
        output.write(sb.toString());
    }
}
