package me.nasukhov.study;

import me.nasukhov.db.Connection;

import javax.validation.constraints.NotNull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class QuestionRepository {
    private static final String VARIANTS_DELIMITER = ";";

    private final Connection db;

    public QuestionRepository() {
        db = Connection.getInstance();
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

    public void addUserAnswer(UUID channelQuestionId, String userId, String channelId, boolean isCorrectAnswer) {
        Map<Integer, Object> params = new HashMap<>();
        params.put(1, channelQuestionId);
        params.put(2, userId);
        params.put(3, channelId);
        params.put(4, isCorrectAnswer);
        db.executeQuery("INSERT INTO ch_question_replies(question_id, user_id, channel_id, is_correct) VALUES(?,?,?,?)", params);
    }

    public boolean hasReplyInChannel(String userId, String channelId, UUID channelQuestionId) {
        try {
            Map<Integer, Object> params = new HashMap<>();
            params.put(1, channelId);
            params.put(2, userId);
            params.put(3, channelQuestionId);
            // TODO logically, questions are less frequent and thus should lead index in query
            ResultSet result = db.fetchByQuery("" +
                            "SELECT id FROM ch_question_replies WHERE channel_id=? AND user_id=? AND question_id=? LIMIT 1",
                    params
            );

            boolean hasReply = result.next();
            result.close();

            return hasReply;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ChannelQuestion findQuestionInChannel(UUID channelQuestionId) {
        try {
            ResultSet result = db.fetchByQuery("" +
                            "SELECT q.* FROM questions q" +
                            " INNER JOIN ch_questions cq ON cq.question_id=q.id" +
                            " WHERE cq.id=?" +
                            " LIMIT 1",
                    new HashMap<>() {{
                        put(1, channelQuestionId);
                    }}
            );

            if (!result.next()) {
                return null;
            }

            Question question = mapData(result);
            result.close();

            return new ChannelQuestion(channelQuestionId, question);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public ChannelQuestion createRandomForChannel(String channelId) {
        UUID uuid = UUID.randomUUID();

        try {
            ResultSet result = db.fetchByQuery(
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
                // TODO
                throw new RuntimeException("No more unanswered questions left for channel");
            }
            Question question = mapData(result);
            result.close();

            db.executeQuery(
                    "INSERT INTO ch_questions(id, channel_id, question_id, created_at) VALUES(?, ?, ?, CURRENT_TIMESTAMP)",
                    new HashMap<>() {{
                        put(1, uuid);
                        put(2, channelId);
                        put(3, question.id());
                    }}
            );

            return new ChannelQuestion(uuid, question);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private Question mapData(ResultSet result) throws SQLException {
        return new Question(
                result.getInt("id"),
                result.getString("text"),
                result.getString("answer"),
                Arrays.asList(result.getString("variants").split(VARIANTS_DELIMITER))
        );
    }
}
