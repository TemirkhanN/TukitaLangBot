package me.nasukhov.TukitaLearner.bot.command;

import me.nasukhov.TukitaLearner.bot.io.*;
import me.nasukhov.TukitaLearner.study.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
public class AskQuestionTest {
    private static final String TEMPLATE_ASK_QUESTION = "qh answer %s %d";

    @Autowired
    private AskQuestion handler;

    @Autowired
    private GroupQuestionRepository groupQuestionRepository;

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
    void handleAskWhenAllQuestionsAreAnswered() {
        var channel = new Channel("SomeChannelId");
        var user = new User("SomeId", "SomeName");
        Input input = new Input("/ask", channel, user);

        questionRepository.deleteAll();

        handler.handle(input, output);

        assertTrue(groupQuestionRepository.findAll().isEmpty());
        verify(output).write("У нас пока нет новых вопросов. Проверьте позже");
    }

    @ParameterizedTest
    @ValueSource(strings = {"/ask", "/ask@botName"})
    void handleAsk(String rawInput) {
        var channel = new Channel("SomeChannelId");
        var user = new User("SomeId", "SomeName");
        Input input = new Input(rawInput, channel, user);

        assertTrue(groupQuestionRepository.findAll().isEmpty());

        handler.handle(input, output);

        var allQuestions = groupQuestionRepository.findAll();

        assertEquals(1, allQuestions.size(), "Expected one question");
        var groupQuestion = allQuestions.getFirst();
        assertEquals("2+5 equals to", groupQuestion.getText());
        assertArrayEquals(new String[] {"thirteen", "seven", "twenty-one"}, groupQuestion.listVariants().toArray(new String[0]));

        verify(output, only()).promptChoice("2+5 equals to", new HashMap<>() {{
            put("thirteen", String.format(TEMPLATE_ASK_QUESTION, groupQuestion.getId(), 1));
            put("seven", String.format(TEMPLATE_ASK_QUESTION, groupQuestion.getId(), 2));
            put("twenty-one", String.format(TEMPLATE_ASK_QUESTION, groupQuestion.getId(), 3));
        }});
    }
}
