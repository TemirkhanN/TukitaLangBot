package me.nasukhov.bot.command;

import me.nasukhov.bot.Command;
import me.nasukhov.bot.study.ProgressRepository;
import me.nasukhov.dictionary.DictionaryRepository;
import me.nasukhov.dictionary.Word;

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
        int newLastLearnedWord = 0;

        StringBuilder sb = new StringBuilder();
        for (Word word : dictionary.getChunk(3, lastLearnedWord)) {
            System.out.println(word.description());
            sb.append(word.word());
            sb.append(" - ");
            sb.append(word.translation());
            sb.append("\n");
            sb.append(word.description());
            sb.append("\n\n");
            newLastLearnedWord = word.id();
        }

        if (newLastLearnedWord != 0) {
            progressRepository.setLastLearnedWord(command.channel(), newLastLearnedWord);
        }

        command.channel().sendMessage(sb.toString());
    }
}
