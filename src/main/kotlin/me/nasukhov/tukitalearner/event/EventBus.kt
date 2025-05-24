package me.nasukhov.tukitalearner.event

import java.util.concurrent.ConcurrentHashMap

class EventBus : Dispatcher {
    private val subscribers = ConcurrentHashMap<Class<*>, MutableList<Subscriber<Any>>>()

    fun addSubscriber(subscriber: Subscriber<Any>) {
        val eventId: Class<*> = subscriber.subscribedFor()
        subscribers.putIfAbsent(eventId, ArrayList())
        subscribers[eventId]?.add(subscriber)
    }

    override fun signal(event: Object) {
        val eventId: Class<Any> = event.javaClass
        subscribers.get(eventId)?.forEach({ it.handle(event) })
    }
}
