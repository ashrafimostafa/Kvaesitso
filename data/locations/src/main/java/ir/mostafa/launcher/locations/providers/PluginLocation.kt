package ir.mostafa.launcher.locations.providers

import android.content.Context
import android.graphics.drawable.Drawable
import ir.mostafa.launcher.locations.PluginLocationSerializer
import ir.mostafa.launcher.plugin.config.StorageStrategy
import ir.mostafa.launcher.search.Location
import ir.mostafa.launcher.search.SavableSearchable
import ir.mostafa.launcher.search.SearchableSerializer
import ir.mostafa.launcher.search.UpdatableSearchable
import ir.mostafa.launcher.search.UpdateResult
import ir.mostafa.launcher.search.location.Address
import ir.mostafa.launcher.search.location.Attribution
import ir.mostafa.launcher.search.location.Departure
import ir.mostafa.launcher.search.location.LocationIcon
import ir.mostafa.launcher.search.location.OpeningSchedule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class PluginLocation(
    override val latitude: Double,
    override val longitude: Double,
    override val fixMeUrl: String?,
    override val icon: LocationIcon?,
    override val category: String?,
    override val address: Address?,
    override val openingSchedule: OpeningSchedule?,
    override val websiteUrl: String?,
    override val phoneNumber: String?,
    override val emailAddress: String?,
    override val userRating: Float?,
    override val userRatingCount: Int?,
    override val departures: List<Departure>?,
    override val label: String,
    override val timestamp: Long,
    override val attribution: Attribution?,
    override val updatedSelf: (suspend (SavableSearchable) -> UpdateResult<Location>)?,
    override val labelOverride: String? = null,
    val authority: String,
    val id: String,
    val storageStrategy: StorageStrategy,
) : Location, UpdatableSearchable<Location> {
    override val key: String
        get() = "$domain://$authority:$id"

    override fun overrideLabel(label: String): PluginLocation {
        return this.copy(labelOverride = label)
    }

    override val domain: String = DOMAIN

    override fun getSerializer(): SearchableSerializer {
        return PluginLocationSerializer()
    }

    override suspend fun getProviderIcon(context: Context): Drawable? {
        return withContext(Dispatchers.IO) {
            context.packageManager.resolveContentProvider(authority, 0)
                ?.loadIcon(context.packageManager)
        }
    }

    companion object {
        const val DOMAIN = "plugin.location"
    }
}