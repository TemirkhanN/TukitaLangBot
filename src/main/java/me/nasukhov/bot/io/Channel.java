package me.nasukhov.bot.io;

import me.nasukhov.ServiceLocator;

public class Channel {
    public final String id;
    private final boolean isPublic;

    public Channel(String id) {
        this.id = id;
        this.isPublic = true;
    }

    public Channel(String id, boolean isPublic) {
        this.id = id;
        this.isPublic = isPublic;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public boolean isActive() {
        return ServiceLocator.instance.locate(ChannelRepository.class).isActive(this);
    }

    public void activate() {
        ServiceLocator.instance.locate(ChannelRepository.class).activateChannel(this, true);
    }

    public void deactivate() {
        ServiceLocator.instance.locate(ChannelRepository.class).activateChannel(this, false);
    }
}
