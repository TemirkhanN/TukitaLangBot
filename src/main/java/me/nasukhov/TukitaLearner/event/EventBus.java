package me.nasukhov.TukitaLearner.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventBus implements Dispatcher {
    private final HashMap<Class<?>, List<Subscriber<?>>> subscribers = new HashMap<>();

    public void addSubscriber(Subscriber<?> subscriber) {
        Class<?> eventId = subscriber.subscribedFor();
        subscribers.putIfAbsent(eventId, new ArrayList<>());
        subscribers.get(eventId).add(subscriber);
    }

    @Override
    public void signal(Object event) {
        Class<?> eventId = event.getClass();
        if (!subscribers.containsKey(eventId)) {
            return;
        }

        // TODO this typecast is odd and I don't fully grasp why I need to do this.
        subscribers.get(eventId).forEach(s -> ((Subscriber<Object>)s).handle(event));
    }
}
