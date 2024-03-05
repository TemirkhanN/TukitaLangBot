package me.nasukhov.bot.command;

import me.nasukhov.bot.Command;
import me.nasukhov.study.ProgressRepository;
import me.nasukhov.dictionary.DictionaryRepository;
import me.nasukhov.dictionary.Word;

import java.util.ArrayList;
import java.util.List;

public class LearnWordHandler implements Handler {
    private final DictionaryRepository dictionary;
    private final ProgressRepository progressRepository;

    public LearnWordHandler(DictionaryRepository dictionaryRepository, ProgressRepository progressRepository) {
        this.dictionary = dictionaryRepository;
        this.progressRepository = progressRepository;
    }

    @Override
    public void handle(Command command) {
        int lastLearnedWord = progressRepository.getLastLearnedWordId(command.channel());
        List<Integer> newWordsIds = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        for (Word word : dictionary.getChunk(3, lastLearnedWord)) {
            System.out.println(word.description());
            sb.append(word.word());
            sb.append(" - ");
            sb.append(word.translation());
            sb.append("\n");
            sb.append(word.description());
            sb.append("\n\n");
            newWordsIds.add(word.id());
        }

        if (sb.isEmpty()) {
            return;
        }

        progressRepository.setLastLearnedWords(command.channel(), newWordsIds);

        command.reply(sb.toString());
    }
}
