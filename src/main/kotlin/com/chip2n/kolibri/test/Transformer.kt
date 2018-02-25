package com.chip2n.kolibri.test

import com.chip2n.kolibri.EventStream
import com.chip2n.kolibri.StateUpdate
import com.chip2n.kolibri.StateUpdateStream
import com.chip2n.kolibri.distinctStateChanges
import com.chip2n.kolibri.test.view.TestViewModel

class TestTransformer {
    fun transform(upstream: StateUpdateStream<TestState, TestEvent>): EventStream<TestEvent> {
        return viewModelTransformer(upstream)
    }

    private fun viewModelTransformer(upstream: StateUpdateStream<TestState, TestEvent>): EventStream<TestEvent> {
        val initial = TestEvent.ViewModelUpdate(TestViewModel(title = "im useing startWith"))
        return createViewModelTransformer(upstream, initial) { (oldState, newState, event) ->
            System.out.println("Transformer is running: $oldState -> $newState")
            val model = TestViewModel(title = "Button was pressed I guess")
            TestEvent.ViewModelUpdate(model)
        }
    }

    private fun createViewModelTransformer(
            upstream: StateUpdateStream<TestState, TestEvent>,
            initialEvent: TestEvent,
            mapper: (StateUpdate<TestState, TestEvent>) -> TestEvent
    ) = upstream.distinctStateChanges()
            .map(mapper)
            .startWith(initialEvent)
            .distinctUntilChanged()
}
