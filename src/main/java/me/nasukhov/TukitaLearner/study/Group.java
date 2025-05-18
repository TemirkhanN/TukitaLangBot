package me.nasukhov.TukitaLearner.study;

import jakarta.persistence.EntityManager;

public record Group(String id) {
    public Preferences preferences(EntityManager db) {
        return new Preferences(this, db);
    }
}
