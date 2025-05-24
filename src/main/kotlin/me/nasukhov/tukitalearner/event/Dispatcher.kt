package me.nasukhov.tukitalearner.event

interface Dispatcher {
    fun signal(event: Object)
}
