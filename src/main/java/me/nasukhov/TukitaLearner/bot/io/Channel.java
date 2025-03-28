package me.nasukhov.TukitaLearner.bot.io;

import me.nasukhov.TukitaLearner.DI.ServiceLocator;

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
        return ServiceLocator.getInstance().locate(ChannelRepository.class).isActive(this);
    }

    public boolean isRegistered() {
        return ServiceLocator.getInstance().locate(ChannelRepository.class).findById(this.id).isPresent();
    }

    public void activate() {
        ServiceLocator.getInstance().locate(ChannelRepository.class).activateChannel(this, true);
    }

    public void deactivate() {
        ServiceLocator.getInstance().locate(ChannelRepository.class).activateChannel(this, false);
    }
}
