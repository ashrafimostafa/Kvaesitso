package ir.mostafa.launcher.ktx

import kotlin.math.ceil

fun Double.ceilToInt(): Int {
    return ceil(this).toInt()
}
