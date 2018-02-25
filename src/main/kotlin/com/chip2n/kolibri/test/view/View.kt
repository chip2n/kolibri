package com.chip2n.kolibri.test.view

import com.chip2n.kolibri.View
import com.chip2n.kolibri.test.TestEvent
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class TestView : View<TestViewModel, TestEvent> {
    override val events: Observable<TestEvent> =
            Observable.just(TestEvent.View.TapSomeButton as TestEvent)
                    .delay(1, TimeUnit.SECONDS)

    override fun render(model: TestViewModel) {
        System.out.println("rendering model: $model")
    }
}
