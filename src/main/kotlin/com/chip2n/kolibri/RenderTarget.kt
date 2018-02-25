package com.chip2n.kolibri

interface RenderTarget<in M> {
    fun render(model: M)
}
