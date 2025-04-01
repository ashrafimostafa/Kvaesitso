package ir.mostafa.launcher.searchable

import ir.mostafa.launcher.search.SavableSearchable

internal fun SavableSearchable.serialize(): String? {
    val serializer = getSerializer()
    return serializer.serialize(this)
}
