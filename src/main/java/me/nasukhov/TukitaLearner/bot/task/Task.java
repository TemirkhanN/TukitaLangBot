package me.nasukhov.TukitaLearner.bot.task;

import jakarta.persistence.*;
import me.nasukhov.TukitaLearner.bot.io.Channel;

import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "task_name")
    private String name;

    @ManyToOne
    private Channel channel;

    @Column(nullable = false, name = "frequency")
    private int frequencyInMinutes;

    @Column(nullable = false)
    private boolean isActive = true;

    @Column
    private LocalDateTime lastExecutedAt;

    @Column(nullable = false)
    private LocalDateTime nextExecutionAt;

    protected Task() {
        // required by JPA
    }

    public Task(String name, int frequencyInMinutes, Channel channel) {
        this.name = name;
        this.frequencyInMinutes = frequencyInMinutes;
        this.channel = channel;
        this.nextExecutionAt = LocalDateTime.now().plusMinutes(frequencyInMinutes);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Channel getChannel() {
        return channel;
    }

    public int getFrequencyInMinutes() {
        return frequencyInMinutes;
    }

    public void setLastExecutedAt(LocalDateTime time) {
        lastExecutedAt = time;
        // TODO check if hibernate can handle dateval change by reference
        nextExecutionAt = nextExecutionAt.plusMinutes(frequencyInMinutes);
    }

    public LocalDateTime getLastExecutedAt() {
        return lastExecutedAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void deactivate() {
        isActive = false;
    }

    public LocalDateTime getNextExecutionAt() {
        return nextExecutionAt;
    }
}