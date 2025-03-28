package me.nasukhov.bot.TukitaLearner.command;

import me.nasukhov.TukitaLearner.bot.command.AskQuestion;
import me.nasukhov.TukitaLearner.bot.io.*;
import me.nasukhov.TukitaLearner.study.GroupQuestion;
import me.nasukhov.TukitaLearner.study.ProgressRepository;
import me.nasukhov.TukitaLearner.study.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class AskQuestionTest {
    private static final String TEMPLATE_ASK_QUESTION = "qh answer %s %d";
    private AskQuestion handler;
    private ProgressRepository progressRepository;
    private Output output;

    @BeforeEach
    void setup() {
        output = mock(Output.class);
        progressRepository = mock(ProgressRepository.class);

        handler = new AskQuestion(progressRepository);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/ask", "/ask@botName"})
    void testHandleAsk(String rawInput) {
        UUID chQuestionId = UUID.randomUUID();

        GroupQuestion groupQuestion = new GroupQuestion(
                chQuestionId,
                new Question(123, "2+5 equals to", "seven", new ArrayList<>(){{
                    add("thirteen");
                    add("seven");
                    add("twenty-one");
                }})
        );

        when(progressRepository.createRandomForChannel("SomeChannelId")).thenReturn(Optional.of(groupQuestion));

        Input input = new Input(
                rawInput,
                new Channel("SomeChannelId"),
                new User("SomeUserId", "SomeUserName")
        );
        handler.handle(input, output);

        verify(output, only()).promptChoice("2+5 equals to", new HashMap<>() {{
            put("thirteen", String.format(TEMPLATE_ASK_QUESTION, chQuestionId, 1));
            put("seven", String.format(TEMPLATE_ASK_QUESTION, chQuestionId, 2));
            put("twenty-one", String.format(TEMPLATE_ASK_QUESTION, chQuestionId, 3));
        }});
    }
}
