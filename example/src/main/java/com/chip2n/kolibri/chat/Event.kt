package com.chip2n.kolibri.chat

sealed class Event {
    // Remote events
    data class NewMessage(val message: String) : Event()
    object StartTyping : Event()

    // View events
    data class SendMessage(val message: String) : Event()

    // Side effects
    data class ViewModelUpdated(val model: ViewModel) : Event()
}
