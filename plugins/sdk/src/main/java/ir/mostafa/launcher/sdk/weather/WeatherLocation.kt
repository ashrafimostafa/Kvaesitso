package ir.mostafa.launcher.sdk.weather

sealed interface WeatherLocation {
    val name: String

    data class Id(override val name: String, val id: String) : WeatherLocation

    data class LatLon(override val name: String, val lat: Double, val lon: Double) :
        WeatherLocation

    data object Managed: WeatherLocation {
        override val name: String = ""
    }
}
