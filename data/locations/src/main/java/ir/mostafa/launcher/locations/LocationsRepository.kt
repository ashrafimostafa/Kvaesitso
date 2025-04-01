package ir.mostafa.launcher.locations

import android.content.Context
import ir.mostafa.launcher.devicepose.DevicePoseProvider
import ir.mostafa.launcher.locations.providers.PluginLocationProvider
import ir.mostafa.launcher.locations.providers.openstreetmaps.OsmLocationProvider
import ir.mostafa.launcher.permissions.PermissionGroup
import ir.mostafa.launcher.permissions.PermissionsManager
import ir.mostafa.launcher.preferences.search.LocationSearchSettings
import ir.mostafa.launcher.search.Location
import ir.mostafa.launcher.search.SearchableRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

internal class LocationsRepository(
    private val context: Context,
    private val settings: LocationSearchSettings,
    private val poseProvider: DevicePoseProvider,
    private val permissionsManager: PermissionsManager,
) : SearchableRepository<Location> {

    override fun search(
        query: String,
        allowNetwork: Boolean
    ): Flow<ImmutableList<Location>> {
        if (query.isBlank()) {
            return flowOf(persistentListOf())
        }

        val hasPermission = permissionsManager.hasPermission(PermissionGroup.Location)

        return combineTransform(settings.data, hasPermission) { settingsData, permission ->
            emit(persistentListOf())
            if (!permission || settingsData.providers.isEmpty()) {
                return@combineTransform
            }

            val userLocation = poseProvider.getLocation().firstOrNull()
                ?: poseProvider.lastLocation
                ?: return@combineTransform

            val providers = settingsData.providers.map {
                when (it) {
                    "openstreetmaps" -> OsmLocationProvider(context, settings)
                    else -> PluginLocationProvider(context, it)
                }
            }

            supervisorScope {
                val result = MutableStateFlow(persistentListOf<Location>())

                for (provider in providers) {
                    launch {
                        val r = provider.search(
                            query,
                            userLocation,
                            allowNetwork,
                            settingsData.searchRadius,
                            settingsData.hideUncategorized
                        )
                        result.update {
                            (it + r).toPersistentList()
                        }
                    }
                }
                emitAll(result)
            }
        }
    }
}