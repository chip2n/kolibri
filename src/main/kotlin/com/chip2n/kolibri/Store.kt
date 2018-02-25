package com.chip2n.kolibri

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.observables.ConnectableObservable
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.schedulers.Schedulers
import kotlin.reflect.KClass

typealias EventStream<E> = Observable<E>
typealias StateUpdateStream<S, E> = Observable<StateUpdate<S, E>>
data class StateUpdate<out S, out E>(val oldState: S, val newState: S, val event: E)

abstract class Store<S, E>(initial: S) {
    val sideEffects: ConnectableObservable<E> = Observable.create<E> { observer ->
        events
                .observeOn(Schedulers.io())
                .withLatestFrom(state) { event, oldState ->
                    System.out.println("new event: $event, oldState: $oldState")
                    val newState = reduce(oldState, event)
                    state.accept(newState)
                    StateUpdate(oldState, newState, event)
                }
                .compose(this::transform)
                .subscribe { event ->
                    observer.onNext(event)
                    events.accept(event)
                }
    }
            .publish()

    private val state = BehaviorRelay.createDefault(initial)
    private val events = PublishRelay.create<E>()

    abstract fun reduce(state: S, event: E): S
    abstract fun transform(upstream: StateUpdateStream<S, E>): EventStream<E>

    fun attachEventSource(eventSource: EventStream<E>): Disposable {
        System.out.println("Attached an event source")
        return eventSource.subscribe(events)
    }
}

fun <S, E> createStore(
        initialState: S,
        reducer: (S, E) -> S,
        transformer: (StateUpdateStream<S, E>) -> EventStream<E>
): Store<S, E> {
    return object : Store<S, E>(initialState) {
        override fun reduce(state: S, event: E) = reducer(state, event)
        override fun transform(upstream: StateUpdateStream<S, E>) = transformer(upstream)
    }
}

inline fun <S, E, reified T> StateUpdateStream<S, E>.ofEventType()
        : StateUpdateStream<S, T> =
        filter { it.event is T }
                .map {
                    @Suppress("UNCHECKED_CAST")
                    it as StateUpdate<S, T>
                }

inline fun <S, E, reified T : Any> StateUpdateStream<S, E>.ofEventType(@Suppress("unused") type: KClass<T>)
        : StateUpdateStream<S, T> = ofEventType()

fun <S, E> StateUpdateStream<S, E>.distinctStateChanges(): StateUpdateStream<S, E> =
        distinctUntilChanged { old, new -> old.newState == new.newState }
