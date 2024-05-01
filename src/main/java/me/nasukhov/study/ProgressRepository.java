package me.nasukhov.study;

import me.nasukhov.db.Collection;
import me.nasukhov.db.Connection;

import java.util.*;

public class ProgressRepository {
    private final Connection db;
    private final QuestionRepository questionRepository;

    public ProgressRepository(Connection db, QuestionRepository questionRepository) {
        this.db = db;
        this.questionRepository = questionRepository;
    }

    public GroupStats getGroupStats(Group group) {
        List<StudentStats> usersStats = new ArrayList<>();
        Map<Integer, Object> params = new HashMap<>() {{
            put(1, group.id());
        }};
        Collection result = db.fetchByQuery("SELECT " +
                        "user_id, " +
                        "SUM(CASE WHEN is_correct = true THEN 1 ELSE 0 END) AS correct_answers, " +
                        "COUNT(*) AS total_answers " +
                        "FROM ch_question_replies " +
                        "WHERE channel_id=? " +
                        "GROUP BY user_id ORDER BY total_answers DESC",
                params
        );

        while (result.next()) {
            int correctAnswers = ((Long)result.getCurrentEntryProp("correct_answers")).intValue();
            int totalAnswers = ((Long)result.getCurrentEntryProp("total_answers")).intValue();
            usersStats.add(
                    new StudentStats(
                            result.getCurrentEntryProp("user_id"),
                            correctAnswers,
                            totalAnswers - correctAnswers
                    )
            );
        }

        result.free();

        return new GroupStats(group, usersStats);
    }

    public int getLastLearnedWordId(Group by) {
        Map<Integer, Object> params = new HashMap<>() {{
            put(1, by.id());
        }};

        Collection result = db.fetchByQuery("SELECT resource_id FROM learned_resources WHERE group_id=? AND resource_type='word' ORDER BY learned_at DESC LIMIT 1", params);
        int wordId = 0;
        if (result.next()) {
            wordId = result.getCurrentEntryProp("resource_id");
        }
        result.free();

        return wordId;
    }

    public void setLastLearnedWords(Group by, List<Integer> wordIds) {
        for (Integer wordId : wordIds) {
            db.executeQuery(
                    "INSERT INTO learned_resources(group_id, resource_id, resource_type, learned_at) VALUES (?, ?, 'word', CURRENT_TIMESTAMP)",
                    new HashMap<>() {{
                        put(1, by.id());
                        put(2, wordId);
                    }}
            );
        }
    }

    public void addUserAnswer(UUID channelQuestionId, String userId, String channelId, boolean isCorrectAnswer) {
        questionRepository.addUserAnswer(channelQuestionId, userId, channelId, isCorrectAnswer);
    }

    public boolean hasReplyInChannel(String userId, String channelId, UUID channelQuestionId) {
        return questionRepository.hasReplyInChannel(userId, channelId, channelQuestionId);
    }

    public Optional<GroupQuestion> createRandomForChannel(String channelId) {
        return questionRepository.createRandomForChannel(channelId);
    }

    public Optional<GroupQuestion> findQuestionInChannel(UUID channelQuestionId) {
        return questionRepository.findQuestionInChannel(channelQuestionId);
    }

    public Optional<String> nextRandomFact(Group group) {
        Map<Integer, Object> params = new HashMap<>() {{
            put(1, group.id());
        }};

        Collection result = db.fetchByQuery("SELECT f.id as factId, f.text as fact FROM facts f LEFT JOIN learned_resources lr ON lr.group_id=? AND lr.resource_id=f.id AND lr.resource_type='fact' WHERE lr.id IS NULL LIMIT 1", params);
        if (result.next()) {
            int factId = result.getCurrentEntryProp("factId");

            db.executeQuery(
                    "INSERT INTO learned_resources(group_id, resource_id, resource_type, learned_at) VALUES (?, ?, 'fact', CURRENT_TIMESTAMP)",
                    new HashMap<>() {{
                        put(1, group.id());
                        put(2, factId);
                    }}
            );

            return Optional.of(result.getCurrentEntryProp("fact"));
        }
        result.free();

        return Optional.empty();
    }
}
