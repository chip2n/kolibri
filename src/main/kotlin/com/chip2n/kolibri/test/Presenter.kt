package com.chip2n.kolibri.test

import com.chip2n.kolibri.Presenter
import com.chip2n.kolibri.test.view.TestView
import io.reactivex.Observable
import io.reactivex.rxkotlin.ofType

fun <T : Any> Observable<T>.mirrorLatest(): Observable<T> = replay(1).autoConnect(-1)

class TestPresenter(reducer: TestReducer, transformer: TestTransformer) : Presenter<TestState, TestEvent, TestView>(
        initialState = TestState.initial(),
        reducer = reducer::reduce,
        transformer = transformer::transform
) {
    private val viewModelUpdates = sideEffects
            .ofType<TestEvent.ViewModelUpdate>()
            .mirrorLatest()

    init {
        sideEffects.connect()
    }

    override fun onAttach(source: TestView) {
        viewModelUpdates.map { it.model }
                .subscribe(source::render)
    }
}
