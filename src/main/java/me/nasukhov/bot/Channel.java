package me.nasukhov.bot;

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
}
