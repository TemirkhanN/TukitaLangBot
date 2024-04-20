package me.nasukhov.bot.bridge.event;

import me.nasukhov.bot.io.BotJoinedChannel;
import me.nasukhov.bot.io.ChannelRepository;
import me.nasukhov.event.Subscriber;

public class EnableForChannelSubscriber implements Subscriber<BotJoinedChannel> {
    private final ChannelRepository channelRepository;

    public EnableForChannelSubscriber(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Override
    public Class<BotJoinedChannel> subscribedFor() {
        return BotJoinedChannel.class;
    }

    @Override
    public void handle(BotJoinedChannel event) {
        channelRepository.activateChannel(event.channel(), true);
    }
}
