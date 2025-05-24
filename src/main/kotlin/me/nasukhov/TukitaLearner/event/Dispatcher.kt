package me.nasukhov.TukitaLearner.event

interface Dispatcher {
    fun signal(event: Object)
}
