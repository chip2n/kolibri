package com.chip2n.kolibri.test

data class TestState(val number: Int) {
    companion object {
        fun initial() = TestState(number = 1337)
    }
}
