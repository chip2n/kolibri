package com.chip2n.kolibri.counter

sealed class Event {
    // View events
    object TapMinusButton : Event()
    object TapPlusButton : Event()

    // Side effects
    data class ViewModelUpdated(val model: ViewModel) : Event()
}
