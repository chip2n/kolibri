package com.chip2n.kolibri

import io.reactivex.disposables.Disposable

abstract class Presenter<S, E, in V : EventSource<E>> constructor(
        initialState: S,
        reducer: Reducer<S, E>,
        transformer: Transformer<S, E> = emptyTransformer(),
        logger: Logger<S, E>? = null
) {
    private val store = Store(initialState, reducer, transformer, logger)
    protected val sideEffects = store.effects

    private val disposables: HashMap<EventSource<E>, Disposable> = hashMapOf()

    protected open fun onAttach(source: V) {}
    protected open fun onDetach(source: V) {}

    fun attach(source: V) {
        val disposable = source.events.subscribe(store::send)
        registerDisposable(source, disposable)
        onAttach(source)
    }

    fun detach(source: V) {
        unregisterDisposable(source)
        onDetach(source)
    }

    protected fun send(event: E) = store.send(event)

    protected fun registerDisposable(view: V, disposable: Disposable) {
        disposables[view] = disposable
    }

    private fun unregisterDisposable(view: V) {
        disposables.remove(view)?.dispose()
    }
}
