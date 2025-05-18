package me.nasukhov.TukitaLearner.bot.io;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "channels")
public class Channel {
    @Id
    public String id;

    @Column(nullable = false)
    private boolean isPublic;

    @Column(nullable = false)
    private boolean isActive = true;

    @Column(nullable = false)
    private LocalDateTime addedAt;

    protected Channel() {

    }

    public Channel(String id) {
        this.id = id;
        this.isPublic = true;
        addedAt = LocalDateTime.now();
    }

    public Channel(String id, boolean isPublic) {
        this.id = id;
        this.isPublic = isPublic;
        addedAt = LocalDateTime.now();
    }

    public boolean isPublic() {
        return isPublic;
    }

    public boolean isActive() {
        return isActive;
    }

    public void deactivate() {
        isActive = false;
    }

    public void activate() {
        isActive = true;
    }

    public LocalDateTime getRegistrationDate() {
        return addedAt;
    }
}
