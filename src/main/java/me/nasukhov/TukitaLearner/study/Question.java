package me.nasukhov.TukitaLearner.study;

import jakarta.persistence.*;
import me.nasukhov.TukitaLearner.db.StringListConverter;
import org.springframework.data.annotation.Immutable;

import java.util.List;

@Entity
@Immutable
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    public String text;

    @Column(nullable = false)
    public String answer;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "TEXT")
    public List<String> variants;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupQuestion> groupQuestions;

    protected Question() {

    }

    public Question(
            String text,
            String answer,
            List<String> variants
    ) {
        this.text = text;
        this.answer = answer;
        this.variants = variants;
    }
}
