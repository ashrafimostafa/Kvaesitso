package ir.mostafa.launcher.sdk.locations

data class LocationQuery(
    val query: String,
    val userLatitude: Double,
    val userLongitude: Double,
    val searchRadius: Long,
)
