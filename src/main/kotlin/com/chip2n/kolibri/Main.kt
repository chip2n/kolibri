package com.chip2n.kolibri

import com.chip2n.kolibri.test.TestPresenter
import com.chip2n.kolibri.test.TestReducer
import com.chip2n.kolibri.test.TestTransformer
import com.chip2n.kolibri.test.view.TestView

fun main(args: Array<String>) {
    val reducer = TestReducer()
    val transformer = TestTransformer()
    val presenter = TestPresenter(reducer, transformer)
    val view = TestView()
    presenter.attach(view)

    readLine()
    System.out.println("Bye!")
}