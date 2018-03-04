package com.chip2n.kolibri.chat

import android.util.Log
import com.chip2n.kolibri.Logger

internal class LogcatLogger : Logger<State, Event> {
    companion object {
        private const val TAG = "KolibriExample"
    }

    override fun logEvent(event: Event) {
        Log.d(TAG, "event: $event")
    }

    override fun logStateChange(oldState: State, newState: State) {
        Log.d(TAG, "state change: $oldState -> $newState")
    }
}
