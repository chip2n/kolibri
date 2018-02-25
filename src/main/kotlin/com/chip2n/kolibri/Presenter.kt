package com.chip2n.kolibri

import io.reactivex.Observable
import io.reactivex.disposables.Disposable

abstract class Presenter<S, E, in V : EventSource<E>> constructor(
        initialState: S,
        reducer: (S, E) -> S,
        transformer: (Observable<StateUpdate<S, E>>) -> Observable<E>
) {
    private val store = createStore(initialState, reducer, transformer)
    protected val sideEffects = store.sideEffects

    private val disposables: HashMap<EventSource<E>, Disposable> = hashMapOf()

    abstract protected fun onAttach(source: V)

    fun attach(source: V) {
        val disposable = store.attachEventSource(source.events)
        registerDisposable(source, disposable)
        onAttach(source)
    }

    fun detach(source: V) {
        unregisterDisposable(source)
    }

    private fun registerDisposable(view: V, disposable: Disposable) {
        disposables[view] = disposable
    }

    private fun unregisterDisposable(view: V) {
        disposables.remove(view)?.dispose()
    }
}
