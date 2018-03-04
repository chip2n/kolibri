package com.chip2n.kolibri.counter

import android.util.Log
import com.chip2n.kolibri.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.ofType

class CounterPresenter : Presenter<State, Event, CounterView>(
        initialState = State(0),
        reducer = { state, event ->
            when (event) {
                Event.TapMinusButton -> state.decrement()
                Event.TapPlusButton -> state.increment()
                else -> state
            }
        },
        transformer = { upstream: StateUpdateStream<State, Event> ->
            upstream.distinctStateChanges()
                    .map {
                        val model = ViewModel("Count: ${it.newState.counter}")
                        Event.ViewModelUpdated(model)
                    }

        },
        logger = object: Logger<State, Event> {
            override fun logEvent(event: Event) { Log.i("Example", "event: $event") }
            override fun logStateChange(oldState: State, newState: State) { Log.i("Example", "state change: $oldState -> $newState") }
        }
) {
    private val viewModelUpdates = sideEffects
            .ofType<Event.ViewModelUpdated>()
            .map { it.model }
            .startWith(ViewModel("Count: 0"))
            .mirrorLatest()
            .observeOn(AndroidSchedulers.mainThread())

    override fun onAttach(source: CounterView) {
        registerDisposable(source, viewModelUpdates.subscribe(source::render))
    }
}

fun <T : Any> Observable<T>.mirrorLatest(): Observable<T> = replay(1).autoConnect(-1)
