package ir.mostafa.launcher.plugin.contracts

import ir.mostafa.launcher.search.location.Address
import ir.mostafa.launcher.search.location.Attribution
import ir.mostafa.launcher.search.location.Departure
import ir.mostafa.launcher.search.location.LocationIcon
import ir.mostafa.launcher.search.location.OpeningSchedule

abstract class LocationPluginContract {
    object Paths {
        const val Search = "search"
        const val Get = "get"
    }

    object Params {
        /**
         * Search query.
         * Type: String
         */
        const val Query = "query"

        /**
         * Latitude of user's current location in degrees.
         * Type: Double?
         */
        const val UserLatitude = "user_latitude"

        /**
         * Longitude of user's current location in degrees.
         * Type: Double?
         */
        const val UserLongitude = "user_longitude"

        /**
         * Search radius in meters.
         * Type: Long
         */
        const val SearchRadius = "search_radius"

        /**
         * Whether to allow network requests.
         * Type: Boolean
         */
        const val AllowNetwork = "network"
    }

    object GetParams {
        /**
         * Unique identifier of location to look up.
         * Type: String
         */
        const val Id = "id"
    }

    object LocationColumns : Columns() {
        /**
         * Unique identifier of location.
         * Type: String
         */
        val Id = column<String>("id")

        /**
         * Display name of location.
         * Type: String
         */
        val Label = column<String>("label")

        /**
         * Latitude of location in degrees.
         * Type: Double
         */
        val Latitude = column<Double>("latitude")

        /**
         * Longitude of location in degrees.
         * Type: Double
         */
        val Longitude = column<Double>("longitude")

        /**
         * Url for users to report / fix incorrect data.
         * Type: String?
         */
        val FixMeUrl = column<String>("fix_me_url")

        /**
         * Icon of location.
         * Type: String? (enum LocationIcon)
         */
        val Icon = column<LocationIcon>("icon")

        /**
         * Location category.
         * Type: String?
         */
        val Category = column<String>("category")

        /**
         * Street name of location.
         * Type: String? (JSON)
         */
        val Address = column<Address>("address")

        /**
         * Opening schedule of location, encoded as JSON.
         * Type: String? (JSON)
         */
        val OpeningSchedule = column<OpeningSchedule>("opening_schedule")

        /**
         * Website URL of location.
         * Type: String?
         */
        val WebsiteUrl = column<String>("website_url")

        /**
         * Phone number of location.
         * Type: String?
         */
        val PhoneNumber = column<String>("phone_number")

        val EmailAddress = column<String>("email_address")

        /**
         * User rating of location, from 0.0 (worst) to 1.0 (best)
         * Type: Float?
         */
        val UserRating = column<Float>("user_rating")
        val UserRatingCount = column<Int>("user_rating_count")

        /**
         * Public transport departures originating from this location, encoded as JSON.
         * Type: String? (JSON)
         */
        val Departures = column<List<Departure>>("departures")

        /**
         * Attribution information for location data.
         * Type: String? (JSON)
         */
        val Attribution = column<Attribution>("attribution")
    }
}