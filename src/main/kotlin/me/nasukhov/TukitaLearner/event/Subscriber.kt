package me.nasukhov.TukitaLearner.event

interface Subscriber<T> {
    fun subscribedFor(): Class<T>

    fun handle(event: T)
}
