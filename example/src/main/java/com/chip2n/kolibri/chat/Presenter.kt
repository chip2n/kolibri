package com.chip2n.kolibri.chat

import android.util.Log
import com.chip2n.kolibri.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.ofType
import java.util.concurrent.TimeUnit

class ChatPresenter : Presenter<State, Event, ChatView>(
        initialState = State(listOf()),
        reducer = { state, event ->
            when (event) {
                is Event.NewMessage -> {
                    state.copy(
                            messages = state.messages.plus(ChatMessage("Someone", event.message)),
                            isTyping = false
                    )
                }
                is Event.SendMessage -> {
                    state.copy(
                            messages = state.messages.plus(ChatMessage("Someone", event.message))
                    )
                }
                Event.StartTyping -> state.copy(isTyping = true)
                else -> state
            }
        },
        transformer = { upstream: StateUpdateStream<State, Event> ->
            upstream.distinctStateChanges()
                    .map {
                        val model = ViewModel(
                                "Friend",
                                it.newState.messages.map { it.message },
                                it.newState.isTyping)
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
            .startWith(ViewModel("Friend"))
            .mirrorLatest()
            .observeOn(AndroidSchedulers.mainThread())

    init {
        val remoteMessages = listOf(
                Event.StartTyping to 1000L,
                Event.NewMessage("Hello") to 1000L,
                Event.StartTyping to 1000L,
                Event.NewMessage("I'm simulating a real chat bot!") to 1500L,
                Event.StartTyping to 1000L,
                Event.NewMessage("It seems like I'm writing stuff, but really I'm just delaying my responses") to 2000L
        )
        Observable.fromIterable(remoteMessages)
                .concatMap { Observable.just(it.first).delay(it.second, TimeUnit.MILLISECONDS) }
                .subscribe(this::send)
    }

    override fun onAttach(source: ChatView) {
        registerDisposable(source, viewModelUpdates.subscribe(source::render))
    }
}

fun <T : Any> Observable<T>.mirrorLatest(): Observable<T> = replay(1).autoConnect(-1)
