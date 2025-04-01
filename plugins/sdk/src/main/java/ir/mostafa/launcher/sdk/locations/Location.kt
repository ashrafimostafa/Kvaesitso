package ir.mostafa.launcher.sdk.locations

import ir.mostafa.launcher.search.location.Address
import ir.mostafa.launcher.search.location.Attribution
import ir.mostafa.launcher.search.location.Departure
import ir.mostafa.launcher.search.location.LocationIcon
import ir.mostafa.launcher.search.location.OpeningSchedule

data class Location(
    val id: String,
    val label: String,
    val latitude: Double,
    val longitude: Double,
    val icon: LocationIcon? = null,
    /**
     * Human-readable category of the location.
     * Should be localized.
     */
    val category: String?,
    val address: Address? = null,
    val openingSchedule: OpeningSchedule? = null,
    val websiteUrl: String? = null,
    val phoneNumber: String? = null,
    val emailAddress: String? = null,
    /**
     * User rating of a location, from 0 to 1.
     * Will be multiplied by 5 to get a 5-star rating.
     */
    val userRating: Float? = null,
    /**
     * Number of reviews that were used to calculate the user rating.
     */
    val userRatingCount: Int? = null,
    val departures: List<Departure>? = null,
    val fixMeUrl: String? = null,
    val attribution: Attribution? = null,
)