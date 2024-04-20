package me.nasukhov.event;

public interface Subscriber<T> {
    Class<T> subscribedFor();

    void handle(T event);
}
