package com.chip2n.kolibri.common

import android.util.DisplayMetrics

fun Float.pxToDp(displayMetrics: DisplayMetrics) = this / displayMetrics.density
fun Float.pxToSp(displayMetrics: DisplayMetrics) = this / displayMetrics.scaledDensity

fun Float.dpToPx(displayMetrics: DisplayMetrics) = this * displayMetrics.density
fun Float.spToPx(displayMetrics: DisplayMetrics) = this * displayMetrics.scaledDensity
