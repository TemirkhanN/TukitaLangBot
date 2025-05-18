package me.nasukhov.TukitaLearner.bot.command;

import me.nasukhov.TukitaLearner.bot.io.*;
import me.nasukhov.TukitaLearner.study.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
class AnswerQuestionTest {
    @Autowired AnswerQuestion handler;
    @Autowired AskQuestion askQuestion;
    @Autowired ProgressTracker progressTracker;
    @Autowired GroupQuestionRepository groupQuestionRepository;

    @Autowired
    private QuestionRepository questionRepository;

    private Output output;

    @BeforeEach
    void setup() {
        output = mock(Output.class);

        questionRepository.save(
            new Question("2+5 equals to", "seven", new ArrayList<>(){{
                add("thirteen");
                add("seven");
                add("twenty-one");
            }})
        );
    }

    @AfterEach
    void reset() {
        questionRepository.deleteAll();
    }

    @Test
    void handleCorrect() {
        var channel = new Channel("SomeChannelId");
        var user = new User("SomeId", "SomeName");

        var questionResult = progressTracker.createRandomForChannel(channel);
        assertTrue(questionResult.isPresent());
        var newGroupQuestion = questionResult.get();
        assertFalse(newGroupQuestion.hasAnswerFromUser(user.name()));

        var questionId = newGroupQuestion.getId().toString();
        var correctAnswerId = getCorrectVariant(newGroupQuestion);

        Input input = new Input(String.format("qh answer %s %d", questionId, correctAnswerId), channel, user);

        handler.handle(input, output);

        verify(output).write(String.format("%s, да, правильный ответ «<spoiler>%s</spoiler>».", user.name(), newGroupQuestion.viewAnswer()));
    }

    private int getCorrectVariant(GroupQuestion groupQuestion) {
        var variantId = 0;

        var correctAnswer = groupQuestion.viewAnswer();
        while (!groupQuestion.viewVariant(variantId).equals(correctAnswer)) {
            if (variantId > 1000) {
                throw new RuntimeException("Looks like an endless loop");
            }

            variantId++;
        }

        return variantId;
    }
}
