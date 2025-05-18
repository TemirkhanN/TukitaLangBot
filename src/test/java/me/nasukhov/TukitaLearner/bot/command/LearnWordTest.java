package me.nasukhov.TukitaLearner.bot.command;

import me.nasukhov.TukitaLearner.bot.io.Channel;
import me.nasukhov.TukitaLearner.bot.io.Input;
import me.nasukhov.TukitaLearner.bot.io.Output;
import me.nasukhov.TukitaLearner.bot.io.User;
import me.nasukhov.TukitaLearner.dictionary.DictionaryRepository;
import me.nasukhov.TukitaLearner.dictionary.Word;
import me.nasukhov.TukitaLearner.study.Group;
import me.nasukhov.TukitaLearner.study.LearnedResourceRepository;
import me.nasukhov.TukitaLearner.study.ProgressTracker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.mockito.Mockito.*;

@SpringBootTest
public class LearnWordTest {
    @Autowired
    private LearnWord handler;

    @Autowired
    private DictionaryRepository dictionary;

    @Autowired
    private ProgressTracker progressTracker;

    @Autowired
    private LearnedResourceRepository learnedResourceRepository;

    private Output output;

    @BeforeEach
    void setup() {
        output = mock(Output.class);

        dictionary.saveAll(List.of(
            new Word("гьой", "собака", "Делает кусь"),
            new Word("кету", "кошка", "Для глажки"),
            new Word("рухья", "дерево", "Вырабатывает кислород ми спасает от жары")
        ));
    }

    @AfterEach
    void reset() {
        learnedResourceRepository.deleteAll();
        dictionary.deleteAll();
    }

    @Test
    void handleUnsupportedInput() {
        var channel = new Channel("");
        var user = new User("SomeId", "SomeName");
        Input input = new Input("learn", channel, user);

        handler.handle(input, output);

        verifyNoInteractions(output);
    }

    @Test
    void handleWhenAllWordsAreLearned() {
        var channel = new Channel("");
        var user = new User("SomeId", "SomeName");
        Input input = new Input("/learn", channel, user);

        progressTracker.setLastLearnedWords(new Group(channel.id), dictionary.findAll());

        handler.handle(input, output);

        verify(output).write("Вы изучили все слова из нашего словаря - больше новых слов нет.");
        verifyNoMoreInteractions(output);
    }

    @Test
    void handleNormally() {
        var channel = new Channel("");
        var user = new User("SomeId", "SomeName");
        Input input = new Input("/learn", channel, user);

        handler.handle(input, output);

        verify(output).write("""
                гьой - собака
                
                кету - кошка
                
                рухья - дерево"""
        );
    }
}
