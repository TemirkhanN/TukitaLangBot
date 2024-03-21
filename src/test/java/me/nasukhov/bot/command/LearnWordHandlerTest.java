package me.nasukhov.bot.command;

import me.nasukhov.bot.Channel;
import me.nasukhov.bot.Command;
import me.nasukhov.bot.Output;
import me.nasukhov.bot.User;
import me.nasukhov.dictionary.DictionaryRepository;
import me.nasukhov.dictionary.PartOfSpeech;
import me.nasukhov.dictionary.Word;
import me.nasukhov.study.ProgressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class LearnWordHandlerTest {
    private static final int MAX_AMOUNT_OF_WORDS_PER_REQUEST = 3;

    private LearnWordHandler handler;
    private DictionaryRepository dictionaryRepository;
    private ProgressRepository progressRepository;
    private Output output;

    @BeforeEach
    void setup() {
        output = mock(Output.class);
        dictionaryRepository = mock(DictionaryRepository.class);
        progressRepository = mock(ProgressRepository.class);
        handler = new LearnWordHandler(dictionaryRepository, progressRepository);
    }

    @Test
    void testHandleUnsupportedCommand() {
        Command cmd = new Command(
                "Some input",
                new Channel("SomeChannelId", output),
                new User("SomeUserId", "SomeUserName")
        );

        handler.handle(cmd);

        verifyNoInteractions(output, dictionaryRepository, progressRepository);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/learn", "/learn@botName"})
    public void testLearnWordsWhenThereAreNoneLeft(String input) {
        Command cmd = new Command(
                input,
                new Channel("SomeChannelId", output),
                new User("SomeUserId", "SomeUserName")
        );

        int lastLearnedWordId = 123;
        when(progressRepository.getLastLearnedWordId(cmd.channel())).thenReturn(lastLearnedWordId);
        when(dictionaryRepository.getChunk(MAX_AMOUNT_OF_WORDS_PER_REQUEST, lastLearnedWordId)).thenReturn(new ArrayList<>());

        handler.handle(cmd);

        verify(output).write("Вы изучили все слова из нашего словаря - больше новых слов нет.");
        verify(progressRepository).getLastLearnedWordId(cmd.channel());
        verifyNoMoreInteractions(progressRepository);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/learn", "/learn@botName"})
    public void testLearnWords(String input) {
        Command cmd = new Command(
                input,
                new Channel("SomeChannelId", output),
                new User("SomeUserId", "SomeUserName")
        );

        int lastLearnedWordId = 0;
        when(progressRepository.getLastLearnedWordId(cmd.channel())).thenReturn(lastLearnedWordId);
        when(dictionaryRepository.getChunk(MAX_AMOUNT_OF_WORDS_PER_REQUEST, lastLearnedWordId)).thenReturn(new ArrayList<>(){{
            add(new Word(124, "Word1", "Translation1", "Description1", PartOfSpeech.NOUN, "Some context"));
            add(new Word(125, "Word2", "Translation2", "Description2", PartOfSpeech.NOUN, "Another context"));
        }});

        handler.handle(cmd);

        verify(output).write("Word1 - Translation1\n\nWord2 - Translation2");
        verify(progressRepository).getLastLearnedWordId(cmd.channel());
        verify(progressRepository).setLastLearnedWords(cmd.channel(), List.of(124, 125));
        verifyNoMoreInteractions(progressRepository);
    }
}
