package com.chip2n.kolibri.common

import android.util.DisplayMetrics

fun Int.pxToDp(displayMetrics: DisplayMetrics) = (this / displayMetrics.density).toInt()
fun Int.pxToSp(displayMetrics: DisplayMetrics) = (this / displayMetrics.scaledDensity).toInt()

fun Int.dpToPx(displayMetrics: DisplayMetrics) = (this * displayMetrics.density).toInt()
fun Int.spToPx(displayMetrics: DisplayMetrics) = (this * displayMetrics.scaledDensity).toInt()
