package com.po4yka.gallerypicker.utils

import kotlin.math.abs

infix fun Float.eq(other: Float) = abs(this - other) < FloatUtils.DELTA

infix fun Float.ge(other: Float) = this > other || this.eq(other)

private object FloatUtils {
    const val DELTA = 0.01f
}
