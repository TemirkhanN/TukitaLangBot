package me.nasukhov.study;

import me.nasukhov.bot.Channel;
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

    public int getLastLearnedWordId(Channel by) {
        Map<Integer, Object> params = new HashMap<>(){{
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
        for (Integer wordId: wordIds) {
            db.executeQuery(
                    "INSERT INTO learned_words(channel_id, word_id, learned_at) VALUES (?, ?, CURRENT_TIMESTAMP)",
                    new HashMap<>(){{
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
