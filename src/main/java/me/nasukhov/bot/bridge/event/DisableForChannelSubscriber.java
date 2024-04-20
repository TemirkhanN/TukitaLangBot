package me.nasukhov.bot.bridge.event;

import me.nasukhov.bot.io.BotLeftChannel;
import me.nasukhov.bot.io.ChannelRepository;
import me.nasukhov.event.Subscriber;

public class DisableForChannelSubscriber implements Subscriber<BotLeftChannel> {
    private final ChannelRepository channelRepository;

    public DisableForChannelSubscriber(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }
    @Override
    public Class<BotLeftChannel> subscribedFor() {
        return BotLeftChannel.class;
    }

    @Override
    public void handle(BotLeftChannel event) {
        channelRepository.activateChannel(event.channel(), false);
    }
}
