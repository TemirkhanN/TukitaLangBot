package me.nasukhov.bot.command;

import me.nasukhov.bot.*;
import me.nasukhov.study.ChannelQuestion;
import me.nasukhov.study.ProgressRepository;
import me.nasukhov.study.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class AnswerQuestionTest {
    private AnswerQuestion handler;
    private AskQuestion askQuestion;
    private ProgressRepository progressRepository;
    private Output output;

    @BeforeEach
    void setup() {
        output = mock(Output.class);
        progressRepository = mock(ProgressRepository.class);
        askQuestion = mock(AskQuestion.class);

        handler = new AnswerQuestion(progressRepository, askQuestion);
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
    public void testHandleIncorrectAnswer() {
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
                "qh answer " + questionId + " 3",
                new Channel("SomeChannelId"),
                new User("SomeUserId", "SomeUserName")
        );

        when(progressRepository.hasReplyInChannel("SomeUserId", "SomeChannelId", questionId)).thenReturn(false);
        when(progressRepository.findQuestionInChannel(questionId)).thenReturn(Optional.of(channelQuestion));

        handler.handle(input, output);

        verify(progressRepository).addUserAnswer(questionId, "SomeUserId", "SomeChannelId", false);
        verify(output).write("SomeUserName, увы, правильный ответ «seven».");
    }

    @Test
    public void testHandleCorrectAnswer() {
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
        verify(output).write("SomeUserName, да, правильный ответ «seven».");
    }

    @Test
    public void testHandleCorrectAnswerInDirectMessages() {
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
                new Channel("SomeChannelId", false),
                new User("SomeUserId", "SomeUserName")
        );

        when(progressRepository.hasReplyInChannel("SomeUserId", "SomeChannelId", questionId)).thenReturn(false);
        when(progressRepository.findQuestionInChannel(questionId)).thenReturn(Optional.of(channelQuestion));

        handler.handle(input, output);

        verify(progressRepository).addUserAnswer(questionId, "SomeUserId", "SomeChannelId", true);
        verify(output).write("SomeUserName, да, правильный ответ «seven».");
        verify(askQuestion).handle(input, output);
    }

    @Test
    public void testHandleIncorrectAnswerInDirectMessages() {
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
                "qh answer " + questionId + " 3",
                new Channel("SomeChannelId", false),
                new User("SomeUserId", "SomeUserName")
        );

        when(progressRepository.hasReplyInChannel("SomeUserId", "SomeChannelId", questionId)).thenReturn(false);
        when(progressRepository.findQuestionInChannel(questionId)).thenReturn(Optional.of(channelQuestion));

        handler.handle(input, output);

        verify(progressRepository).addUserAnswer(questionId, "SomeUserId", "SomeChannelId", false);
        verify(output).write("SomeUserName, увы, правильный ответ «seven».");
        verify(askQuestion).handle(input, output);
    }
}
