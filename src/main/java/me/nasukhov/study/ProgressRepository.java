package me.nasukhov.study;

import me.nasukhov.bot.Channel;
import me.nasukhov.bot.User;
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

    public ChannelStats getChannelStats(Channel channel) {
        List<UserStats> usersStats = new ArrayList<>();
        Map<Integer, Object> params = new HashMap<>() {{
            put(1, channel.id);
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
                    new UserStats(
                            result.getCurrentEntryProp("user_id"),
                            correctAnswers,
                            totalAnswers - correctAnswers
                    )
            );
        }

        result.free();

        return new ChannelStats(usersStats);
    }

    public int getLastLearnedWordId(Channel by) {
        Map<Integer, Object> params = new HashMap<>() {{
            put(1, by.id);
        }};

        Collection result = db.fetchByQuery("SELECT word_id FROM learned_words WHERE channel_id=? ORDER BY learned_at DESC LIMIT 1", params);
        int wordId = 0;
        if (result.next()) {
            wordId = result.getCurrentEntryProp("word_id");
        }
        result.free();

        return wordId;
    }

    public void setLastLearnedWords(Channel by, List<Integer> wordIds) {
        for (Integer wordId : wordIds) {
            db.executeQuery(
                    "INSERT INTO learned_words(channel_id, word_id, learned_at) VALUES (?, ?, CURRENT_TIMESTAMP)",
                    new HashMap<>() {{
                        put(1, by.id);
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

    public Optional<ChannelQuestion> createRandomForChannel(String channelId) {
        return questionRepository.createRandomForChannel(channelId);
    }

    public Optional<ChannelQuestion> findQuestionInChannel(UUID channelQuestionId) {
        return questionRepository.findQuestionInChannel(channelQuestionId);
    }
}
