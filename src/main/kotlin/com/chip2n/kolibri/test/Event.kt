package com.chip2n.kolibri.test

import com.chip2n.kolibri.test.view.TestViewModel

sealed class TestEvent {
    sealed class View : TestEvent() {
        object TapSomeButton : View()
    }

    data class ViewModelUpdate(val model: TestViewModel) : TestEvent()
}
