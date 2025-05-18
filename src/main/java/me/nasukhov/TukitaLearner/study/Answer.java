package me.nasukhov.TukitaLearner.study;

import jakarta.persistence.*;

@Entity
@Table(name = "ch_question_replies")
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    public String userId;

    @Column(nullable = false)
    public String channelId;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    public GroupQuestion question;

    @Column(nullable = false)
    public boolean isCorrect;

    protected Answer() {

    }

    public Answer(String userId, String channelId, GroupQuestion question, boolean isCorrect) {
        this.userId = userId;
        this.channelId = channelId;
        this.question = question;
        this.isCorrect = isCorrect;
    }
}
