package me.nasukhov.bot.command;

import me.nasukhov.bot.Channel;
import me.nasukhov.bot.Command;
import me.nasukhov.bot.Output;
import me.nasukhov.bot.User;
import me.nasukhov.study.ChannelQuestion;
import me.nasukhov.study.Question;
import me.nasukhov.study.QuestionRepository;
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
    private QuestionRepository questionRepository;

    private Output output;

    @BeforeEach
    void setup() {
        output = mock(Output.class);
        questionRepository = mock(QuestionRepository.class);

        handler = new QuestionHandler(questionRepository);
    }

    @Test
    void testHandleUnsupportedCommand() {
        Command cmd = new Command(
                "Some input",
                new Channel("SomeChannelId", output),
                new User("SomeUserId", "SomeUserName")
        );

        handler.handle(cmd);

        verifyNoInteractions(output);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/ask", "/ask@botName"})
    void testHandleAsk(String input) {
        Command cmd = new Command(
                input,
                new Channel("SomeChannelId", output),
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

        when(questionRepository.createRandomForChannel("SomeChannelId")).thenReturn(Optional.of(channelQuestion));

        handler.handle(cmd);

        verify(output, only()).write("2+5 equals to", new HashMap<>() {{
            put("thirteen", String.format(TEMPLATE_ASK_QUESTION, chQuestionId, 1));
            put("seven", String.format(TEMPLATE_ASK_QUESTION, chQuestionId, 2));
            put("twenty-one", String.format(TEMPLATE_ASK_QUESTION, chQuestionId, 3));
        }});
    }

    @Test
    public void testHandleAnswerWithInvalidInput() {
        Command cmd = new Command(
                "qh answer invalidInput",
                new Channel("SomeChannelId", output),
                new User("SomeUserId", "SomeUserName")
        );

        handler.handle(cmd);

        verifyNoInteractions(output);
    }

    @Test
    public void testHandleAnswerWhenUserHasAlreadyAnsweredThatQuestionBefore() {
        UUID questionId = UUID.fromString("9b740f73-2766-4d31-9029-c910759ad41b");

        Command cmd = new Command(
                "qh answer " + questionId + " 2",
                new Channel("SomeChannelId", output),
                new User("SomeUserId", "SomeUserName")
        );

        when(questionRepository.hasReplyInChannel("SomeUserId", "SomeChannelId", questionId)).thenReturn(true);

        handler.handle(cmd);

        verify(questionRepository).hasReplyInChannel("SomeUserId", "SomeChannelId", questionId);
        verifyNoInteractions(output);
    }

    @Test
    public void testHandleAnswerForQuestionThatWasNotAskedInChannel() {
        UUID questionId = UUID.fromString("9b740f73-2766-4d31-9029-c910759ad41b");

        Command cmd = new Command(
                "qh answer " + questionId + " 2",
                new Channel("SomeChannelId", output),
                new User("SomeUserId", "SomeUserName")
        );

        when(questionRepository.hasReplyInChannel("SomeUserId", "SomeChannelId", questionId)).thenReturn(false);
        when(questionRepository.findQuestionInChannel(questionId)).thenReturn(Optional.empty());

        handler.handle(cmd);

        verify(questionRepository).hasReplyInChannel("SomeUserId", "SomeChannelId", questionId);
        verify(questionRepository).findQuestionInChannel(questionId);
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

        Command cmd = new Command(
                "qh answer " + questionId + " 2",
                new Channel("SomeChannelId", output),
                new User("SomeUserId", "SomeUserName")
        );

        when(questionRepository.hasReplyInChannel("SomeUserId", "SomeChannelId", questionId)).thenReturn(false);
        when(questionRepository.findQuestionInChannel(questionId)).thenReturn(Optional.of(channelQuestion));

        handler.handle(cmd);

        verify(questionRepository).hasReplyInChannel("SomeUserId", "SomeChannelId", questionId);
        verify(questionRepository).findQuestionInChannel(questionId);
        verify(questionRepository).addUserAnswer(questionId, "SomeUserId", "SomeChannelId", true);
        verify(output).write("SomeUserName отвечает правильно на вопрос «2+5 equals to»");
    }
}
