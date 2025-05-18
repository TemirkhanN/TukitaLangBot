package me.nasukhov.TukitaLearner.study;

import jakarta.persistence.*;
import me.nasukhov.TukitaLearner.bot.io.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ch_questions")
public class GroupQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    // TODO generate uuid 7
    private UUID id;

    @Column(nullable = false)
    private String channelId;

    @ManyToOne
    private Question question;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers = new ArrayList<>();

    protected GroupQuestion() {}

    // TODO could id generation encapsulation make it better?
    public GroupQuestion(Channel channel, Question question) {
        this.channelId = channel.id;
        this.question = question;
    }

    public UUID getId() {
        return id;
    }

    public String getText() {
        return question.text;
    }

    private boolean isCorrectAnswer(int numberOfAnswer) {
        String selectedAnswer = viewVariant(numberOfAnswer);
        String correctAnswer = question.answer;

        return selectedAnswer.equals(correctAnswer);
    }

    public String viewAnswer() {
        return question.answer;
    }

    public String viewVariant(int variant) {
        if (variant < 1) {
            return "";
        }

        if (variant > question.variants.size()) {
            return "";
        }

        return question.variants.get(variant-1);
    }

    public List<String> listVariants() {
        return question.variants;
    }

    public Answer addAnswer(String user, int selectedAnswer) {
        if (hasAnswerFromUser(user)) {
            throw new RuntimeException("User has already answered this question");
        }

        var answer = new Answer(
                user,
                channelId,
                this,
                isCorrectAnswer(selectedAnswer)
        );

        answers.add(answer);

        return answer;
    }

    public boolean hasAnswerFromUser(String user) {
        return answers.stream().anyMatch(answer -> answer.userId.equals(user));
    }
}
