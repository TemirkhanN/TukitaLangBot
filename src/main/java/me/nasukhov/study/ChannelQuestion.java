package me.nasukhov.study;

import java.util.List;
import java.util.UUID;

public final class ChannelQuestion {
    private final UUID id;
    private final Question question;

    // TODO could id generation encapsulation make it better?
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

    public boolean isCorrectAnswer(int numberOfAnswer) {
        String selectedAnswer = viewVariant(numberOfAnswer);
        String correctAnswer = question.answer();

        return selectedAnswer.equals(correctAnswer);
    }

    public String viewAnswer() {
        return question.answer();
    }

    public String viewVariant(int variant) {
        if (variant < 0) {
            return "";
        }

        if (variant > question.variants().size()) {
            return "";
        }

        return question.variants().get(variant-1);
    }

    public List<String> listVariants() {
        return question.variants();
    }
}
