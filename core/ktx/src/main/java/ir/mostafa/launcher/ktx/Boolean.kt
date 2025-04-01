package ir.mostafa.launcher.ktx

inline fun Boolean.toInt(): Int {
    return if (this) 1 else 0
}