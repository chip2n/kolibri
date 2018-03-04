package com.chip2n.kolibri.counter

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.chip2n.kolibri.R
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.content_counter.*
import kotlinx.android.synthetic.main.toolbar.*

class CounterActivity : AppCompatActivity(), CounterView {
    override val events: Observable<Event>

    private val eventSubject = PublishSubject.create<Event>()
    private val presenter = CounterPresenter()

    init {
        events = eventSubject
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_counter)
        setSupportActionBar(toolbar)

        minusButton.setOnClickListener { eventSubject.onNext(Event.TapMinusButton) }
        plusButton.setOnClickListener { eventSubject.onNext(Event.TapPlusButton) }
    }

    override fun onStart() {
        super.onStart()
        presenter.attach(this)
    }

    override fun onStop() {
        super.onStop()
        presenter.detach(this)
    }

    override fun render(model: ViewModel) {
        Log.i("ChatExample", "model: $model")
        text.text = model.text
    }
}
