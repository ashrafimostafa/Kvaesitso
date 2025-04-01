package ir.mostafa.launcher.searchable

import ir.mostafa.launcher.database.entities.SavedSearchableEntity
import ir.mostafa.launcher.search.SavableSearchable

data class SavedSearchable(
    val key: String,
    /**
     * null if searchable could not be deserialized (i.e. the app has been uninstalled)
     */
    val searchable: SavableSearchable?,
    var launchCount: Int,
    var pinPosition: Int,
    var visibility: VisibilityLevel,
    var weight: Double
) {
    fun toDatabaseEntity(): SavedSearchableEntity? {
        val data = searchable?.serialize() ?: return null

        return SavedSearchableEntity(
            key = key,
            type = searchable.domain,
            serializedSearchable = data,
            visibility = visibility.value,
            pinPosition = pinPosition,
            launchCount = launchCount,
            weight = weight
        )
    }
}