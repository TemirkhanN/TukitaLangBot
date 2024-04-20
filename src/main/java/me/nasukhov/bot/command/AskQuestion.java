package me.nasukhov.bot.command;

import me.nasukhov.bot.bridge.IOResolver;
import me.nasukhov.bot.io.Channel;
import me.nasukhov.bot.io.ChannelRepository;
import me.nasukhov.bot.io.Input;
import me.nasukhov.bot.io.Output;
import me.nasukhov.study.ChannelQuestion;
import me.nasukhov.study.ProgressRepository;
import me.nasukhov.study.Time;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AskQuestion implements Handler {
    private static final String NO_MORE_QUESTIONS_LEFT = "У нас пока нет новых вопросов. Проверьте позже";
    private final ProgressRepository progressRepository;
    private final ChannelRepository channelRepository;

    public AskQuestion(ProgressRepository progressRepository, ChannelRepository channelRepository) {
        this.progressRepository = progressRepository;
        this.channelRepository = channelRepository;

        registerTasks();
    }

    @Override
    public boolean supports(Input input) {
        return input.isDirectCommand("ask");
    }

    private void registerTasks() {

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable autoAskQuestionInGroups = () -> {
            // We don't want it to work at night
            if (Time.isOffHours()) {
                return;
            }

            for (Channel channel : channelRepository.list()) {
                ask(channel, IOResolver.resolveFor(channel));
            }
        };

        scheduler.scheduleAtFixedRate(autoAskQuestionInGroups, 0, 2, TimeUnit.HOURS);
    }

    @Override
    public void handle(Input input, Output output) {
        ask(input.channel(), output);
    }

    private void ask(Channel channel, Output output) {
        Optional<ChannelQuestion> result = progressRepository.createRandomForChannel(channel.id);

        if (result.isEmpty()) {
            // TODO share summary. reset progress
            output.write(NO_MORE_QUESTIONS_LEFT);

            return;
        }

        ChannelQuestion newQuestion = result.get();

        Map<String, String> replies = new HashMap<>();
        int optionNum = 0;
        for (String replyVariant : newQuestion.listVariants()) {
            replies.put(replyVariant, String.format("qh answer %s %d", newQuestion.getId().toString(), ++optionNum));
        }

        output.promptChoice(newQuestion.getText(), replies);
    }
}
