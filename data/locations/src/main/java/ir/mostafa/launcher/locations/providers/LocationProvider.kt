package ir.mostafa.launcher.locations.providers

import ir.mostafa.launcher.search.Location

internal typealias AndroidLocation = android.location.Location

internal interface LocationProvider<TId> {
    suspend fun search(
        query: String,
        userLocation: AndroidLocation,
        allowNetwork: Boolean,
        searchRadiusMeters: Int,
        hideUncategorized: Boolean
    ): List<Location>
}