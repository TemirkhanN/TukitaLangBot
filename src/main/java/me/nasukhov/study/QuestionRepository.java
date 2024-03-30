package me.nasukhov.study;

import me.nasukhov.db.Collection;
import me.nasukhov.db.Connection;

import java.util.*;

public class QuestionRepository {
    private static final String VARIANTS_DELIMITER = ";";

    private final Connection db;

    public QuestionRepository(Connection db) {
        this.db = db;
    }

    public void create(
            String text,
            String answer,
            List<String> variants
    ) {
        db.executeQuery(
                "INSERT INTO questions(text, answer, variants) VALUES (?, ?, ?)",
                new HashMap<>() {{
                    put(1, text);
                    put(2, answer);
                    put(3, String.join(VARIANTS_DELIMITER, variants));
                }}
        );
    }

    void addUserAnswer(UUID channelQuestionId, String userId, String channelId, boolean isCorrectAnswer) {
        Map<Integer, Object> params = new HashMap<>();
        params.put(1, channelQuestionId);
        params.put(2, userId);
        params.put(3, channelId);
        params.put(4, isCorrectAnswer);
        db.executeQuery("INSERT INTO ch_question_replies(question_id, user_id, channel_id, is_correct) VALUES(?,?,?,?)", params);
    }

    boolean hasReplyInChannel(String userId, String channelId, UUID channelQuestionId) {
        Map<Integer, Object> params = new HashMap<>();
        params.put(1, channelId);
        params.put(2, userId);
        params.put(3, channelQuestionId);
        // TODO logically, questions are less frequent and thus should lead index in the query
        Collection result = db.fetchByQuery("" +
                        "SELECT id FROM ch_question_replies WHERE channel_id=? AND user_id=? AND question_id=? LIMIT 1",
                params
        );

        boolean hasReply = result.next();

        result.free();

        return hasReply;
    }

    Optional<ChannelQuestion> findQuestionInChannel(UUID channelQuestionId) {
        Collection result = db.fetchByQuery("" +
                        "SELECT q.* FROM questions q" +
                        " INNER JOIN ch_questions cq ON cq.question_id=q.id" +
                        " WHERE cq.id=?" +
                        " LIMIT 1",
                new HashMap<>() {{
                    put(1, channelQuestionId);
                }}
        );

        if (!result.next()) {
            return Optional.empty();
        }

        Question question = mapData(result);
        result.free();

        return Optional.of(new ChannelQuestion(channelQuestionId, question));
    }

    Optional<ChannelQuestion> createRandomForChannel(String channelId) {
        Collection result = db.fetchByQuery(
                "SELECT q.* FROM questions q" +
                        " LEFT JOIN ch_questions cq" +
                        " ON cq.question_id=q.id AND cq.channel_id=?" +
                        " WHERE cq.id IS NULL" +
                        " ORDER BY RANDOM()" +
                        " LIMIT 1",
                new HashMap<>() {{
                    put(1, channelId);
                }}
        );

        if (!result.next()) {
            return Optional.empty();
        }

        Question question = mapData(result);
        result.free();

        UUID uuid = UUID.randomUUID();
        db.executeQuery(
                "INSERT INTO ch_questions(id, channel_id, question_id, created_at) VALUES(?, ?, ?, CURRENT_TIMESTAMP)",
                new HashMap<>() {{
                    put(1, uuid);
                    put(2, channelId);
                    put(3, question.id());
                }}
        );

        return Optional.of(new ChannelQuestion(uuid, question));
    }

    private Question mapData(Collection result) {
        return new Question(
                result.getCurrentEntryProp("id"),
                result.getCurrentEntryProp("text"),
                result.getCurrentEntryProp("answer"),
                Arrays.asList(((String) result.getCurrentEntryProp("variants")).split(VARIANTS_DELIMITER))
        );
    }
}
