package com.pixelatedmind.game.tinyuniverse.util
typealias EventListener<T> = (T) -> Unit

class EventBus {
    internal val listeners = mutableMapOf<Class<*>, MutableList<EventListener<*>>>()
    fun <T: Any> register(eventType: Class<T>, callback: EventListener<T>) {
        var eventListeners = listeners[eventType]
        if (eventListeners == null) {
            eventListeners = mutableListOf()
            listeners[eventType] = eventListeners
        }
        eventListeners.add(callback as EventListener<Any>)
    }

    inline fun <reified T : Any> register(noinline callback: EventListener<T>) {
        register(T::class.java, callback)
    }

    fun unregisterAll() {
        listeners.clear()
    }

    fun post(event: Any) {
        listeners[event.javaClass]?.forEach {
            @Suppress("UNCHECKED_CAST")
            (it as EventListener<Any>)(event)
        }
    }
}