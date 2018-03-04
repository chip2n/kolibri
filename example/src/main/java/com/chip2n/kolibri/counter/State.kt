package com.chip2n.kolibri.counter

data class State(val counter: Int) {
    fun increment() = copy(counter = counter + 1)
    fun decrement() = copy(counter = counter - 1)
}

