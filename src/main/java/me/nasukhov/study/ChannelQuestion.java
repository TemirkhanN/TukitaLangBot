package me.nasukhov.study;

import java.util.List;
import java.util.UUID;

public final class ChannelQuestion {
    private final UUID id;
    private final Question question;

    public ChannelQuestion(UUID id, Question question) {
        this.id = id;
        this.question = question;
    }

    public UUID getId() {
        return id;
    }

    public String getText() {
        return question.text();
    }

    public String getAnswer() {
        return question.answer();
    }

    public boolean isCorrectAnswer(int numberOfAnswer) {
        if (numberOfAnswer < 0) {
            return false;
        }

        if (numberOfAnswer > question.variants().size()) {
            return false;
        }


        String selectedAnswer = question.variants().get(numberOfAnswer-1);
        String correctAnswer = question.answer();

        return selectedAnswer.equals(correctAnswer);
    }

    public List<String> listVariants() {
        return question.variants();
    }
}
