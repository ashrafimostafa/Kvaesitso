package ir.mostafa.launcher.ui.ktx

inline operator fun IntRange.inc(): IntRange {
    return start + 1..endInclusive + 1
}

inline operator fun IntRange.dec(): IntRange {
    return start - 1..endInclusive - 1
}