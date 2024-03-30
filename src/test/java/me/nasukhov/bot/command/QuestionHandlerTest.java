package me.nasukhov.bot.command;

import me.nasukhov.bot.*;
import me.nasukhov.study.ChannelQuestion;
import me.nasukhov.study.ProgressRepository;
import me.nasukhov.study.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class QuestionHandlerTest {
    private static final String TEMPLATE_ASK_QUESTION = "qh answer %s %d";

    private QuestionHandler handler;
    private ProgressRepository progressRepository;
    private Output output;

    @BeforeEach
    void setup() {
        output = mock(Output.class);
        progressRepository = mock(ProgressRepository.class);

        handler = new QuestionHandler(progressRepository, mock(ChannelRepository.class));
    }

    @Test
    void testHandleUnsupportedCommand() {
        Input input = new Input(
                "Some input",
                new Channel("SomeChannelId"),
                new User("SomeUserId", "SomeUserName")
        );

        handler.handle(input, output);

        verifyNoInteractions(output, progressRepository);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/ask", "/ask@botName"})
    void testHandleAsk(String rawInput) {
        Input input = new Input(
                rawInput,
                new Channel("SomeChannelId"),
                new User("SomeUserId", "SomeUserName")
        );

        UUID chQuestionId = UUID.randomUUID();

        ChannelQuestion channelQuestion = new ChannelQuestion(
                chQuestionId,
                new Question(123, "2+5 equals to", "seven", new ArrayList<>(){{
                    add("thirteen");
                    add("seven");
                    add("twenty-one");
                }})
        );

        when(progressRepository.createRandomForChannel("SomeChannelId")).thenReturn(Optional.of(channelQuestion));

        handler.handle(input, output);

        verify(output, only()).promptChoice("2+5 equals to", new HashMap<>() {{
            put("thirteen", String.format(TEMPLATE_ASK_QUESTION, chQuestionId, 1));
            put("seven", String.format(TEMPLATE_ASK_QUESTION, chQuestionId, 2));
            put("twenty-one", String.format(TEMPLATE_ASK_QUESTION, chQuestionId, 3));
        }});
    }

    @Test
    public void testHandleAnswerWithInvalidInput() {
        Input input = new Input(
                "qh answer invalidInput",
                new Channel("SomeChannelId"),
                new User("SomeUserId", "SomeUserName")
        );

        handler.handle(input, output);
        verifyNoInteractions(output, progressRepository);
    }

    @Test
    public void testHandleAnswerWhenUserHasAlreadyAnsweredThatQuestionBefore() {
        UUID questionId = UUID.fromString("9b740f73-2766-4d31-9029-c910759ad41b");

        Input input = new Input(
                "qh answer " + questionId + " 2",
                new Channel("SomeChannelId"),
                new User("SomeUserId", "SomeUserName")
        );

        when(progressRepository.hasReplyInChannel("SomeUserId", "SomeChannelId", questionId)).thenReturn(true);

        handler.handle(input, output);

        verify(progressRepository).hasReplyInChannel("SomeUserId", "SomeChannelId", questionId);
        verifyNoMoreInteractions(progressRepository);
        verifyNoInteractions(output);
    }

    @Test
    public void testHandleAnswerForQuestionThatWasNotAskedInChannel() {
        UUID questionId = UUID.fromString("9b740f73-2766-4d31-9029-c910759ad41b");

        Input input = new Input(
                "qh answer " + questionId + " 2",
                new Channel("SomeChannelId"),
                new User("SomeUserId", "SomeUserName")
        );

        when(progressRepository.hasReplyInChannel("SomeUserId", "SomeChannelId", questionId)).thenReturn(false);
        when(progressRepository.findQuestionInChannel(questionId)).thenReturn(Optional.empty());

        handler.handle(input, output);

        verify(progressRepository).hasReplyInChannel("SomeUserId", "SomeChannelId", questionId);
        verify(progressRepository).findQuestionInChannel(questionId);
        verifyNoMoreInteractions(progressRepository);
        verifyNoInteractions(output);
    }

    @Test
    public void testHandleAnswerCorrectChoice() {
        UUID questionId = UUID.fromString("9b740f73-2766-4d31-9029-c910759ad41b");

        ChannelQuestion channelQuestion = new ChannelQuestion(
                questionId,
                new Question(123, "2+5 equals to", "seven", new ArrayList<>(){{
                    add("thirteen");
                    add("seven");
                    add("twenty-one");
                }})
        );

        Input input = new Input(
                "qh answer " + questionId + " 2",
                new Channel("SomeChannelId"),
                new User("SomeUserId", "SomeUserName")
        );

        when(progressRepository.hasReplyInChannel("SomeUserId", "SomeChannelId", questionId)).thenReturn(false);
        when(progressRepository.findQuestionInChannel(questionId)).thenReturn(Optional.of(channelQuestion));

        handler.handle(input, output);

        verify(progressRepository).addUserAnswer(questionId, "SomeUserId", "SomeChannelId", true);
        verify(output).write("SomeUserName отвечает правильно на вопрос «2+5 equals to»");
    }
}
