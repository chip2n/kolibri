package com.chip2n.kolibri

interface View<in M, E> : RenderTarget<M>, EventSource<E>
