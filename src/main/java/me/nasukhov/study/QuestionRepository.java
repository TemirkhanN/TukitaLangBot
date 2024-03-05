package me.nasukhov.study;

import me.nasukhov.db.Connection;

import java.util.HashMap;
import java.util.List;

public class QuestionRepository {
    private final Connection db;

    public QuestionRepository() {
        db = Connection.getInstance();
    }

    public Question create(
            String text,
            String answer,
            List<String> variants
    ) {
        db.executeQuery(
                "INSERT INTO questions(text, answer, variants) VALUES (?, ?, ?)",
                new HashMap<>(){{
                    put(1, text);
                    put(2, answer);
                    put(3, String.join(";", variants));
                }}
        );

        return new Question(text, answer, variants);
    }
}
