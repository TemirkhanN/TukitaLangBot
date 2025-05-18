package me.nasukhov.TukitaLearner.study;

import me.nasukhov.TukitaLearner.bot.io.Channel;
import me.nasukhov.TukitaLearner.dictionary.Word;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProgressTracker {

    private final LearnedResourceRepository learnedResourceRepository;
    private final QuestionRepository questionRepository;
    private final StatsRepository statsRepository;
    private final GroupQuestionRepository groupQuestionRepository;
    private final FactRepository factRepository;

    public ProgressTracker(
            LearnedResourceRepository learnedResourceRepository,
            QuestionRepository questionRepository,
            StatsRepository statsRepository,
            GroupQuestionRepository groupQuestionRepository,
            FactRepository factRepository
    ) {
        this.learnedResourceRepository = learnedResourceRepository;
        this.questionRepository = questionRepository;
        this.statsRepository = statsRepository;
        this.groupQuestionRepository = groupQuestionRepository;
        this.factRepository = factRepository;
    }

    public GroupStats getGroupStats(Group group) {
        List<StudentStats> stats = statsRepository.getStatsForGroup(group.id());
        return new GroupStats(group, stats);
    }

    public Long getLastLearnedWordId(Group group) {
        return learnedResourceRepository.findLastLearnedWordId(group.id())
                .map(info -> info.resourceId)
                .orElse(0L);
    }

    public void setLastLearnedWords(Group group, List<Word> words) {
        var now = LocalDateTime.now();
        var learnedWords = words.stream()
                .map(word -> new LearnedResource(group.id(), word.id, ResourceType.WORD, now))
                .toList();

        learnedResourceRepository.saveAll(learnedWords);
    }

    public Optional<GroupQuestion> createRandomForChannel(Channel channel) {
        Optional<Question> question = questionRepository.findUnseenRandom(channel.id);

        if (question.isEmpty()) return Optional.empty();

        GroupQuestion groupQuestion = new GroupQuestion(channel, question.get());
        return Optional.of(groupQuestionRepository.save(groupQuestion));
    }

    public Optional<GroupQuestion> findGroupQuestionById(UUID groupQuestionId) {
        return groupQuestionRepository.findByIdWithAnswers(groupQuestionId);
    }

    public Optional<String> nextRandomFact(Group group) {
        Optional<Fact> fact = factRepository.findUnlearnedFact(group.id());

        fact.ifPresent(f -> {
            learnedResourceRepository.save(
                    new LearnedResource(group.id(), f.getId(), ResourceType.FACT, LocalDateTime.now())
            );
        });

        return fact.map(Fact::getText);
    }
}
