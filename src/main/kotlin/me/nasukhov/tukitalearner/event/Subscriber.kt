package me.nasukhov.tukitalearner.event

interface Subscriber<T> {
    fun subscribedFor(): Class<T>

    fun handle(event: T)
}
