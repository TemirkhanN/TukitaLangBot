package me.nasukhov.bot.command;

import me.nasukhov.bot.Command;
import me.nasukhov.study.ProgressRepository;
import me.nasukhov.dictionary.DictionaryRepository;
import me.nasukhov.dictionary.Word;

import java.util.ArrayList;
import java.util.List;

public class LearnWordHandler implements Handler {
    private static final String NO_MORE_UNLEARNED_WORDS = "Вы изучили все слова из нашего словаря - больше новых слов нет.";

    private final DictionaryRepository dictionary;
    private final ProgressRepository progressRepository;

    public LearnWordHandler(DictionaryRepository dictionaryRepository, ProgressRepository progressRepository) {
        this.dictionary = dictionaryRepository;
        this.progressRepository = progressRepository;
    }

    @Override
    public boolean supports(Command command) {
        return command.isDirectCommand("learn");
    }

    @Override
    public void handle(Command command) {
        if (!supports(command)) {
            return;
        }

        int lastLearnedWord = progressRepository.getLastLearnedWordId(command.channel());
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
            command.reply(NO_MORE_UNLEARNED_WORDS);

            return;
        }

        progressRepository.setLastLearnedWords(command.channel(), newWordsIds);

        // Removing trailing newlines
        sb.delete(sb.length() - 2, sb.length());
        command.reply(sb.toString());
    }
}
