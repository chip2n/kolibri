package com.chip2n.kolibri

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.schedulers.Schedulers
import kotlin.reflect.KClass

typealias EventStream<E> = Observable<E>
typealias StateUpdateStream<S, E> = Observable<StateUpdate<S, E>>
data class StateUpdate<out S, out E>(val oldState: S, val newState: S, val event: E)

typealias Transformer<S, E> = (StateUpdateStream<S, E>) -> EventStream<E>
typealias Reducer<S, E> = (S, E) -> S

interface Logger<S, E> {
    fun logEvent(event: E)
    fun logStateChange(oldState: S, newState: S)
}

class Store<S, E>(
        initial: S,
        reducer: Reducer<S, E>,
        transformer: Transformer<S, E> = emptyTransformer(),
        private val logger: Logger<S, E>? = null
) {
    val effects: Observable<E>
    val state: Observable<S>

    private val effectRelay: PublishRelay<E> = PublishRelay.create()
    private val stateRelay = BehaviorRelay.createDefault(initial)
    private val events = PublishRelay.create<E>()

    private val disposables = CompositeDisposable()

    init {
        effects = effectRelay
        state = stateRelay

        disposables.add(events
                .doOnNext { logger?.logEvent(it) }
                .observeOn(Schedulers.io())
                .withLatestFrom(stateRelay) { event, oldState ->
                    val newState = reducer(oldState, event)
                    if (logger != null && oldState != newState) {
                        logger.logStateChange(oldState, newState)
                    }
                    stateRelay.accept(newState)
                    StateUpdate(oldState, newState, event)
                }
                .compose(transformer)
                .subscribe { event ->
                    effectRelay.accept(event)
                    events.accept(event)
                })
    }

    fun send(event: E) {
        events.accept(event)
    }

    fun destroy() {
        disposables.clear()
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

fun <S, E> combineTransformers(transformers: List<Transformer<S, E>>): Transformer<S, E> {
    return { upstream ->
        upstream.publish { shared ->
            Observable.merge(transformers.map { shared.compose(it) })
        }
    }
}

fun <S, E> emptyTransformer(): Transformer<S, E> = { upstream -> upstream.flatMap { Observable.empty<E>() } }
