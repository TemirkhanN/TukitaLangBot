package me.nasukhov.TukitaLearner.study;

import me.nasukhov.TukitaLearner.db.Connection;

public record Group(String id) {
    public Preferences preferences(Connection db) {
        return new Preferences(this, db);
    }
}
