package me.nasukhov.study;

import me.nasukhov.DI.ServiceLocator;
import me.nasukhov.db.Connection;

public record Group(String id) {
    public Preferences preferences() {
        return new Preferences(this, ServiceLocator.instance.locate(Connection.class));
    }
}
