package me.nasukhov.bot.command;

import me.nasukhov.bot.io.Channel;
import me.nasukhov.bot.io.Input;
import me.nasukhov.bot.io.Output;
import me.nasukhov.bot.io.User;
import me.nasukhov.dictionary.DictionaryRepository;
import me.nasukhov.dictionary.PartOfSpeech;
import me.nasukhov.dictionary.Word;
import me.nasukhov.study.Group;
import me.nasukhov.study.ProgressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class LearnWordTest {
    private static final int MAX_AMOUNT_OF_WORDS_PER_REQUEST = 3;

    private LearnWord handler;
    private DictionaryRepository dictionaryRepository;
    private ProgressRepository progressRepository;
    private Output output;

    @BeforeEach
    void setup() {
        output = mock(Output.class);
        dictionaryRepository = mock(DictionaryRepository.class);
        progressRepository = mock(ProgressRepository.class);
        handler = new LearnWord(dictionaryRepository, progressRepository);
    }

    @Test
    void testHandleUnsupportedCommand() {
        Input input = new Input(
                "Some input",
                new Channel("SomeChannelId"),
                new User("SomeUserId", "SomeUserName")
        );

        handler.handle(input, output);

        verifyNoInteractions(output, dictionaryRepository, progressRepository);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/learn", "/learn@botName"})
    public void testLearnWordsWhenThereAreNoneLeft(String rawInput) {
        Input input = new Input(
                rawInput,
                new Channel("SomeChannelId"),
                new User("SomeUserId", "SomeUserName")
        );

        Group group = new Group(input.channel().id);

        int lastLearnedWordId = 123;
        when(progressRepository.getLastLearnedWordId(group)).thenReturn(lastLearnedWordId);
        when(dictionaryRepository.getChunk(MAX_AMOUNT_OF_WORDS_PER_REQUEST, lastLearnedWordId)).thenReturn(new ArrayList<>());

        handler.handle(input, output);

        verify(output).write("Вы изучили все слова из нашего словаря - больше новых слов нет.");
        verify(progressRepository).getLastLearnedWordId(group);
        verifyNoMoreInteractions(progressRepository);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/learn", "/learn@botName"})
    public void testLearnWords(String rawInput) {
        Input input = new Input(
                rawInput,
                new Channel("SomeChannelId"),
                new User("SomeUserId", "SomeUserName")
        );

        Group group = new Group(input.channel().id);
        int lastLearnedWordId = 0;
        when(progressRepository.getLastLearnedWordId(group)).thenReturn(lastLearnedWordId);
        when(dictionaryRepository.getChunk(MAX_AMOUNT_OF_WORDS_PER_REQUEST, lastLearnedWordId)).thenReturn(new ArrayList<>(){{
            add(new Word(124, "Word1", "Translation1", "Description1", PartOfSpeech.NOUN));
            add(new Word(125, "Word2", "Translation2", "Description2", PartOfSpeech.NOUN));
        }});

        handler.handle(input, output);

        verify(output).write("Word1 - Translation1\n\nWord2 - Translation2");
        verify(progressRepository).getLastLearnedWordId(group);
        verify(progressRepository).setLastLearnedWords(group, List.of(124, 125));
        verifyNoMoreInteractions(progressRepository);
    }
}
