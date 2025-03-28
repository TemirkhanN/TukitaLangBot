package me.nasukhov.TukitaLearner.study;

import me.nasukhov.TukitaLearner.DI.ServiceLocator;
import me.nasukhov.TukitaLearner.db.Connection;

public record Group(String id) {
    public Preferences preferences() {
        return new Preferences(this, ServiceLocator.getInstance().locate(Connection.class));
    }
}
