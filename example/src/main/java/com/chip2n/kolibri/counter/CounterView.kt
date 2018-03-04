package com.chip2n.kolibri.counter

import com.chip2n.kolibri.EventSource
import com.chip2n.kolibri.RenderTarget

interface CounterView : EventSource<Event>, RenderTarget<ViewModel>

