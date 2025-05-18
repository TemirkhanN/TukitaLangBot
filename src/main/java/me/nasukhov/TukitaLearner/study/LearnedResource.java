package me.nasukhov.TukitaLearner.study;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "learned_resources")
public class LearnedResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    public String groupId;

    @Column(nullable = false)
    public Long resourceId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public ResourceType resourceType;

    @Column(nullable = false)
    public LocalDateTime learnedAt;

    public LearnedResource() {

    }

    public LearnedResource(String groupId, Long resourceId, ResourceType resourceType, LocalDateTime learnedAt) {
        this.groupId = groupId;
        this.resourceId = resourceId;
        this.resourceType = resourceType;
        this.learnedAt = learnedAt;
    }
}
