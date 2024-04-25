package me.nasukhov.bot.task;

import me.nasukhov.bot.bridge.IOResolver;
import me.nasukhov.bot.io.Channel;
import me.nasukhov.bot.io.ChannelRepository;
import me.nasukhov.bot.io.Output;
import me.nasukhov.study.GroupQuestion;
import me.nasukhov.study.ProgressRepository;
import me.nasukhov.study.Time;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class AskQuestion implements Task {
    private final Frequency frequency;
    private final ChannelRepository channelRepository;
    private final ProgressRepository progressRepository;

    public AskQuestion(ChannelRepository channelRepository, ProgressRepository progressRepository) {
        frequency = new Frequency(1, TimeUnit.MINUTES);
        this.channelRepository = channelRepository;
        this.progressRepository = progressRepository;
    }

    @Override
    public Frequency frequency() {
        return frequency;
    }

    @Override
    public void run() {
        // We don't want it to work at night
        if (Time.isOffHours()) {
            return;
        }

        for (Channel channel : channelRepository.list()) {
            ask(channel);
        }
    }

    public void ask(Channel channel) {
        Optional<GroupQuestion> result = progressRepository.createRandomForChannel(channel.id);
        if (result.isEmpty()) {
            return;
        }

        GroupQuestion newQuestion = result.get();

        Map<String, String> replies = new HashMap<>();
        int optionNum = 0;
        for (String replyVariant : newQuestion.listVariants()) {
            replies.put(replyVariant, String.format("qh answer %s %d", newQuestion.getId().toString(), ++optionNum));
        }

        Output output = IOResolver.resolveFor(channel);
        output.promptChoice(newQuestion.getText(), replies);
    }
}
