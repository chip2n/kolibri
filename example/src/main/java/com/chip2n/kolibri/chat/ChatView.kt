package com.chip2n.kolibri.chat

import com.chip2n.kolibri.EventSource
import com.chip2n.kolibri.RenderTarget

interface ChatView : EventSource<Event>, RenderTarget<ViewModel>

