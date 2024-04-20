package me.nasukhov.event;

public interface Dispatcher {
    void signal(Object event);
}
