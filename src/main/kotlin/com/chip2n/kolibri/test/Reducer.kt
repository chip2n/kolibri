package com.chip2n.kolibri.test

class TestReducer {
    fun reduce(state: TestState, event: TestEvent): TestState {
        System.out.println("reducing: $state with $event")
        return when (event) {
            TestEvent.View.TapSomeButton -> state.copy(number = 123)
            is TestEvent.ViewModelUpdate -> state
        }
    }
}
