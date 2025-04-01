package ir.mostafa.launcher.locations

import android.content.Context
import ir.mostafa.launcher.locations.providers.PluginLocation
import ir.mostafa.launcher.locations.providers.PluginLocationProvider
import ir.mostafa.launcher.locations.providers.openstreetmaps.OsmLocation
import ir.mostafa.launcher.locations.providers.openstreetmaps.OsmLocationProvider
import ir.mostafa.launcher.plugin.PluginRepository
import ir.mostafa.launcher.plugin.config.StorageStrategy
import ir.mostafa.launcher.search.SavableSearchable
import ir.mostafa.launcher.search.SearchableDeserializer
import ir.mostafa.launcher.search.SearchableSerializer
import ir.mostafa.launcher.search.UpdateResult
import ir.mostafa.launcher.search.asUpdateResult
import ir.mostafa.launcher.search.location.Address
import ir.mostafa.launcher.search.location.Attribution
import ir.mostafa.launcher.search.location.Departure
import ir.mostafa.launcher.search.location.LocationIcon
import ir.mostafa.launcher.search.location.OpeningSchedule
import ir.mostafa.launcher.serialization.Json
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

@Serializable
internal data class SerializedLocation(
    val id: String? = null,
    val lat: Double? = null,
    val lon: Double? = null,
    val icon: LocationIcon? = null,
    val category: String? = null,
    val label: String? = null,
    val address: Address? = null,
    val websiteUrl: String? = null,
    val phoneNumber: String? = null,
    val emailAddress: String? = null,
    val userRating: Float? = null,
    val userRatingCount: Int? = null,
    val openingSchedule: OpeningSchedule? = null,
    val timestamp: Long? = null,
    val departures: List<Departure>? = null,
    val fixMeUrl: String? = null,
    val attribution: Attribution? = null,
    val authority: String? = null,
    val storageStrategy: StorageStrategy? = null,
)

internal class OsmLocationSerializer : SearchableSerializer {
    override fun serialize(searchable: SavableSearchable): String {
        searchable as OsmLocation
        return Json.Lenient.encodeToString(
            SerializedLocation(
                id = searchable.id.toString(),
                lat = searchable.latitude,
                lon = searchable.longitude,
                icon = searchable.icon,
                category = searchable.category,
                label = searchable.label,
                address = searchable.address,
                websiteUrl = searchable.websiteUrl,
                phoneNumber = searchable.phoneNumber,
                emailAddress = searchable.emailAddress,
                userRating = searchable.userRating,
                userRatingCount = searchable.userRatingCount,
                openingSchedule = searchable.openingSchedule,
                timestamp = searchable.timestamp,
                departures = searchable.departures,
                fixMeUrl = searchable.fixMeUrl,
            )
        )
    }

    override val typePrefix: String
        get() = "osmlocation"
}

internal class OsmLocationDeserializer(
    private val osmProvider: OsmLocationProvider,
) : SearchableDeserializer {
    override suspend fun deserialize(serialized: String): SavableSearchable? {
        val json = Json.Lenient.decodeFromString<SerializedLocation>(serialized)
        val id = json.id?.toLongOrNull() ?: return null

        return OsmLocation(
            id = id,
            latitude = json.lat ?: return null,
            longitude = json.lon ?: return null,
            icon = json.icon,
            category = json.category,
            label = json.label ?: return null,
            address = json.address,
            websiteUrl = json.websiteUrl,
            phoneNumber = json.phoneNumber,
            emailAddress = json.emailAddress,
            userRating = json.userRating,
            openingSchedule = json.openingSchedule,
            timestamp = json.timestamp ?: return null,
            updatedSelf = {
                osmProvider.update(id)
            }
        )
    }
}

internal class PluginLocationSerializer : SearchableSerializer {
    override fun serialize(searchable: SavableSearchable): String {
        searchable as PluginLocation
        return when (searchable.storageStrategy) {
            StorageStrategy.StoreReference -> Json.Lenient.encodeToString(
                SerializedLocation(
                    id = searchable.id,
                    authority = searchable.authority,
                    storageStrategy = StorageStrategy.StoreReference,
                )
            )

            else -> {
                Json.Lenient.encodeToString(
                    SerializedLocation(
                        id = searchable.id,
                        lat = searchable.latitude,
                        lon = searchable.longitude,
                        icon = searchable.icon,
                        category = searchable.category,
                        label = searchable.label,
                        address = searchable.address,
                        websiteUrl = searchable.websiteUrl,
                        phoneNumber = searchable.phoneNumber,
                        emailAddress = searchable.emailAddress,
                        userRating = searchable.userRating,
                        userRatingCount = searchable.userRatingCount,
                        attribution = searchable.attribution,
                        openingSchedule = searchable.openingSchedule,
                        timestamp = searchable.timestamp,
                        departures = searchable.departures,
                        fixMeUrl = searchable.fixMeUrl,
                        authority = searchable.authority,
                        storageStrategy = searchable.storageStrategy,
                    )
                )
            }
        }
    }

    override val typePrefix: String
        get() = PluginLocation.DOMAIN
}

internal class PluginLocationDeserializer(
    private val context: Context,
    private val pluginRepository: PluginRepository,
) : SearchableDeserializer {
    override suspend fun deserialize(serialized: String): SavableSearchable? {
        val json = Json.Lenient.decodeFromString<SerializedLocation>(serialized)
        val authority = json.authority ?: return null
        val id = json.id ?: return null
        val strategy = json.storageStrategy ?: StorageStrategy.StoreCopy

        val plugin = pluginRepository.get(authority).firstOrNull() ?: return null
        if (!plugin.enabled) return null

        return when (strategy) {
            StorageStrategy.StoreReference -> {
                PluginLocationProvider(context, authority).get(id).getOrNull()
            }

            else -> {
                val timestamp = json.timestamp ?: 0
                PluginLocation(
                    id = id,
                    latitude = json.lat ?: return null,
                    longitude = json.lon ?: return null,
                    icon = json.icon,
                    category = json.category,
                    label = json.label ?: return null,
                    address = json.address,
                    websiteUrl = json.websiteUrl,
                    phoneNumber = json.phoneNumber,
                    emailAddress = json.emailAddress,
                    userRating = json.userRating,
                    userRatingCount = json.userRatingCount,
                    openingSchedule = json.openingSchedule,
                    timestamp = timestamp,
                    departures = json.departures,
                    fixMeUrl = json.fixMeUrl,
                    attribution = json.attribution,
                    authority = authority,
                    storageStrategy = strategy,
                    updatedSelf = {
                        if (it !is PluginLocation) UpdateResult.TemporarilyUnavailable()
                        else PluginLocationProvider(context, authority).refresh(it, timestamp).asUpdateResult()
                    }
                )
            }
        }
    }
}
