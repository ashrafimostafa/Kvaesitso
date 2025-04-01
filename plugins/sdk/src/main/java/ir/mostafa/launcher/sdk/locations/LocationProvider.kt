package ir.mostafa.launcher.sdk.locations

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import ir.mostafa.launcher.plugin.PluginType
import ir.mostafa.launcher.plugin.config.QueryPluginConfig
import ir.mostafa.launcher.plugin.contracts.LocationPluginContract
import ir.mostafa.launcher.plugin.contracts.LocationPluginContract.LocationColumns
import ir.mostafa.launcher.plugin.data.buildCursor
import ir.mostafa.launcher.plugin.data.get
import ir.mostafa.launcher.sdk.base.QueryPluginProvider
import ir.mostafa.launcher.serialization.Json

abstract class LocationProvider(
    config: QueryPluginConfig,
) : QueryPluginProvider<LocationQuery, Location>(config) {

    private val json = Json.Lenient

    final override fun getPluginType(): PluginType {
        return PluginType.LocationSearch
    }

    override fun List<Location>.toCursor(): Cursor {
        return buildCursor(LocationColumns, this) {
            put(LocationColumns.Id, it.id)
            put(LocationColumns.Label, it.label)
            put(LocationColumns.Latitude, it.latitude)
            put(LocationColumns.Longitude, it.longitude)
            put(LocationColumns.FixMeUrl, it.fixMeUrl)
            put(LocationColumns.Icon, it.icon)
            put(LocationColumns.Category, it.category)
            put(LocationColumns.Address, it.address)
            put(LocationColumns.OpeningSchedule, it.openingSchedule)
            put(LocationColumns.WebsiteUrl, it.websiteUrl)
            put(LocationColumns.PhoneNumber, it.phoneNumber)
            put(LocationColumns.EmailAddress, it.emailAddress)
            put(LocationColumns.UserRating, it.userRating)
            put(LocationColumns.UserRatingCount, it.userRatingCount)
            put(LocationColumns.Departures, it.departures)
            put(LocationColumns.Attribution, it.attribution)
        }
    }

    override fun getQuery(uri: Uri): LocationQuery? {
        val query = uri.getQueryParameter(LocationPluginContract.Params.Query) ?: return null
        val searchRadius = uri.getQueryParameter(LocationPluginContract.Params.SearchRadius)?.toLongOrNull() ?: return null
        val lat = uri.getQueryParameter(LocationPluginContract.Params.UserLatitude)?.toDoubleOrNull() ?: return null
        val lon = uri.getQueryParameter(LocationPluginContract.Params.UserLongitude)?.toDoubleOrNull() ?: return null
        return LocationQuery(
            query = query,
            userLatitude = lat,
            userLongitude = lon,
            searchRadius = searchRadius,
        )
    }

    final override fun Bundle.toResult(): Location? {
        return Location(
            id = get(LocationColumns.Id) ?: return null,
            label = get(LocationColumns.Label) ?: return null,
            latitude = get(LocationColumns.Latitude) ?: return null,
            longitude = get(LocationColumns.Longitude) ?:return null,
            fixMeUrl = get(LocationColumns.FixMeUrl),
            icon = get(LocationColumns.Icon),
            category = get(LocationColumns.Category),
            address = get(LocationColumns.Address),
            openingSchedule = get(LocationColumns.OpeningSchedule),
            websiteUrl = get(LocationColumns.WebsiteUrl),
            phoneNumber = get(LocationColumns.PhoneNumber),
            emailAddress = get(LocationColumns.EmailAddress),
            userRating = get(LocationColumns.UserRating),
            userRatingCount = get(LocationColumns.UserRatingCount),
            departures = get(LocationColumns.Departures),
            attribution = get(LocationColumns.Attribution),

        )
    }
}