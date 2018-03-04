package com.chip2n.kolibri.common

import android.util.DisplayMetrics
import android.view.View

val View.displayMetrics: DisplayMetrics
    get() = resources.displayMetrics
