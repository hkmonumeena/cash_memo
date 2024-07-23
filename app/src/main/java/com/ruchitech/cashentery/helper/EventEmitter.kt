package com.ruchitech.cashentery.helper


import android.os.Handler
import android.os.Looper
import com.ruchitech.cashentery.ui.screens.add_transactions.Transaction
import java.util.concurrent.CopyOnWriteArrayList

object EventEmitter {
    private val subscribers = CopyOnWriteArrayList<EventHandler>()
    private val handler = Handler(Looper.getMainLooper())
    private var lastEventTime = 0L
    private val eventInterval = 1000L // 1 second in milliseconds
    private var lastEventMap: MutableMap<Class<out Event>, Long> = mutableMapOf()
    fun subscribe(handler: EventHandler) {
        subscribers.add(handler)
    }

    fun unsubscribe(handler: EventHandler) {
        subscribers.remove(handler)
    }

    fun postEvent(event: Event) {
        val currentTime = System.currentTimeMillis()

        if (shouldCollectEvent(event)) {
            for (subscriber in subscribers) {
                subscriber.handleInternalEvent(event)
            }

            lastEventMap[event.javaClass] = currentTime
        }
    }

    private fun shouldCollectEvent(event: Event): Boolean {
        val currentTime = System.currentTimeMillis()
        val lastEventTime = lastEventMap[event.javaClass] ?: 0L
        val eventInterval = 1000L // 1 second in milliseconds

        return currentTime - lastEventTime >= eventInterval
    }
}

interface EventHandler {
    fun handleInternalEvent(event: Event)
}

sealed class Event {
    class HomeViewModel(
        val refreshPage: Boolean = false,
        val deleteId: String? = null,
        val transaction: Transaction? = null,
    ) : Event()

    class TransactionDetailsViewModel(
        val transaction: Transaction? = null,
    ) : Event()

    class TransactionsViewModel(
        val refreshPage: Boolean = false,
    ) : Event()

}
