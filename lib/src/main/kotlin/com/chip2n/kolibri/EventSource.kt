package com.chip2n.kolibri

import io.reactivex.Observable

interface EventSource<E> {
    val events: Observable<E>
}
