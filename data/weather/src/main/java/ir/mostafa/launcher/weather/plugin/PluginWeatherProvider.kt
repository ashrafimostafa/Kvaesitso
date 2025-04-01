package ir.mostafa.launcher.weather.plugin

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.CancellationSignal
import android.util.Log
import ir.mostafa.launcher.crashreporter.CrashReporter
import ir.mostafa.launcher.plugin.PluginApi
import ir.mostafa.launcher.plugin.config.WeatherPluginConfig
import ir.mostafa.launcher.plugin.contracts.WeatherPluginContract
import ir.mostafa.launcher.plugin.contracts.WeatherPluginContract.ForecastColumns
import ir.mostafa.launcher.plugin.contracts.WeatherPluginContract.LocationColumns
import ir.mostafa.launcher.plugin.data.withColumns
import ir.mostafa.launcher.preferences.weather.WeatherLocation
import ir.mostafa.launcher.weather.Forecast
import ir.mostafa.launcher.weather.WeatherProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

internal class PluginWeatherProvider(
    private val context: Context,
    private val pluginAuthority: String,
) : WeatherProvider {
    override suspend fun getWeatherData(location: WeatherLocation): List<Forecast>? {
        val config = getPluginConfig()
        val uri = Uri.Builder()
            .scheme("content")
            .authority(pluginAuthority)
            .path(WeatherPluginContract.Paths.Forecasts).apply {
                if (config?.managedLocation == true || location is WeatherLocation.Managed) {
                    // no parameters
                } else if (location is WeatherLocation.LatLon) {
                    appendQueryParameter(
                        WeatherPluginContract.ForecastParams.Lat,
                        location.lat.toString()
                    )
                    appendQueryParameter(
                        WeatherPluginContract.ForecastParams.Lon,
                        location.lon.toString()
                    )
                    appendQueryParameter(WeatherPluginContract.ForecastParams.LocationName, location.name)
                } else if (location is WeatherLocation.Id) {
                    appendQueryParameter(
                        WeatherPluginContract.ForecastParams.Id,
                        location.locationId
                    )
                    appendQueryParameter(WeatherPluginContract.ForecastParams.LocationName, location.name)
                }
            }
            .appendQueryParameter(WeatherPluginContract.ForecastParams.Language, getLang())
            .build()

        return getWeatherData(uri)
    }

    override suspend fun getWeatherData(lat: Double, lon: Double): List<Forecast>? {
        val uri = Uri.Builder()
            .scheme("content")
            .authority(pluginAuthority)
            .path(WeatherPluginContract.Paths.Forecasts)
            .appendQueryParameter(WeatherPluginContract.ForecastParams.Lat, lat.toString())
            .appendQueryParameter(WeatherPluginContract.ForecastParams.Lon, lon.toString())
            .appendQueryParameter(WeatherPluginContract.ForecastParams.Language, getLang())
            .build()

        return getWeatherData(uri)
    }

    private suspend fun getWeatherData(uri: Uri): List<Forecast>? = withContext(Dispatchers.IO) {
        val cancellationSignal = CancellationSignal()
        return@withContext suspendCancellableCoroutine {
            it.invokeOnCancellation {
                cancellationSignal.cancel()
            }
            val cursor = try {
                context.contentResolver.query(
                    uri,
                    null,
                    null,
                    cancellationSignal
                )
            } catch (e: Exception) {
                CrashReporter.logException(e)
                it.resume(null)
                return@suspendCancellableCoroutine
            }

            if (cursor == null) {
                it.resume(null)
                return@suspendCancellableCoroutine
            }

            val results = forecastsFromCursor(cursor) ?: emptyList()
            it.resume(results)
        }
    }

    private fun forecastsFromCursor(cursor: Cursor): List<Forecast>? {
        return cursor.use {
            val results = mutableListOf<Forecast>()

            cursor.withColumns(ForecastColumns) {

                while (cursor.moveToNext()) {
                    results += Forecast(
                        timestamp = cursor[ForecastColumns.Timestamp] ?: continue,
                        temperature = cursor[ForecastColumns.Temperature] ?: continue,
                        updateTime = cursor[ForecastColumns.CreatedAt] ?: continue,
                        condition = cursor[ForecastColumns.Condition] ?: continue,
                        icon = getIcon(cursor[ForecastColumns.Icon]?.name ?: continue),
                        location = cursor[ForecastColumns.Location] ?: continue,
                        provider = cursor[ForecastColumns.Provider] ?: continue,
                        providerUrl = cursor[ForecastColumns.ProviderUrl] ?: "",
                        clouds = cursor[ForecastColumns.Clouds],
                        humidity = cursor[ForecastColumns.Humidity]?.toDouble(),
                        precipitation = cursor[ForecastColumns.Precipitation],
                        precipProbability = cursor[ForecastColumns.RainProbability],
                        windSpeed = cursor[ForecastColumns.WindSpeed],
                        windDirection = cursor[ForecastColumns.WindDirection],
                        pressure = cursor[ForecastColumns.Pressure],
                        night = cursor[ForecastColumns.Night] ?: false,
                        minTemp = cursor[ForecastColumns.TemperatureMin],
                        maxTemp =  cursor[ForecastColumns.TemperatureMax],
                    )
                }
            }
            results
        }
    }

    private fun getIcon(icon: String): Int {
        return when (icon) {
            "Clear" -> Forecast.CLEAR
            "Cloudy" -> Forecast.CLOUDY
            "Cold" -> Forecast.COLD
            "Drizzle" -> Forecast.DRIZZLE
            "Haze" -> Forecast.HAZE
            "Fog" -> Forecast.FOG
            "Hail" -> Forecast.HAIL
            "HeavyThunderstorm" -> Forecast.HEAVY_THUNDERSTORM
            "HeavyThunderstormWithRain" -> Forecast.HEAVY_THUNDERSTORM_WITH_RAIN
            "Hot" -> Forecast.HOT
            "MostlyCloudy" -> Forecast.MOSTLY_CLOUDY
            "PartlyCloudy" -> Forecast.PARTLY_CLOUDY
            "Showers" -> Forecast.SHOWERS
            "Sleet" -> Forecast.SLEET
            "Snow" -> Forecast.SNOW
            "Storm" -> Forecast.STORM
            "Thunderstorm" -> Forecast.THUNDERSTORM
            "ThunderstormWithRain" -> Forecast.THUNDERSTORM_WITH_RAIN
            "Wind" -> Forecast.WIND
            "BrokenClouds" -> Forecast.BROKEN_CLOUDS
            else -> Forecast.NONE
        }
    }

    private fun getLang(): String {
        return Locale.getDefault().language
    }

    override suspend fun findLocation(query: String): List<WeatherLocation> = withContext(Dispatchers.IO) {
        val cancellationSignal = CancellationSignal()
        return@withContext suspendCancellableCoroutine {
            it.invokeOnCancellation {
                cancellationSignal.cancel()
            }
            val uri = Uri.Builder()
                .scheme("content")
                .authority(pluginAuthority)
                .path(WeatherPluginContract.Paths.Locations)
                .appendQueryParameter(WeatherPluginContract.LocationParams.Query, query)
                .appendQueryParameter(WeatherPluginContract.LocationParams.Language, getLang())
                .build()

            val cursor = try {
                context.contentResolver.query(
                    uri,
                    null,
                    null,
                    cancellationSignal
                )
            } catch (e: Exception) {
                CrashReporter.logException(e)
                it.resume(emptyList())
                return@suspendCancellableCoroutine
            }

            if (cursor == null) {
                it.resume(emptyList())
                return@suspendCancellableCoroutine
            }

            val results = locationsFromCursor(cursor) ?: emptyList()
            it.resume(results)
        }
    }

    private fun locationsFromCursor(cursor: Cursor): List<WeatherLocation> {
        return cursor.use {
            val results = mutableListOf<WeatherLocation>()

            cursor.withColumns(LocationColumns) {
                while (cursor.moveToNext()) {
                    val lat = cursor[LocationColumns.Lat]
                    val lon = cursor[LocationColumns.Lon]
                    val locationId = cursor[LocationColumns.Id]
                    val name = cursor[LocationColumns.Name] ?: continue

                    if (lat != null && lon != null) {
                        results += WeatherLocation.LatLon(lat = lat, lon = lon, name = name)
                    } else if (locationId != null) {
                        results += WeatherLocation.Id(locationId = locationId, name = name)
                    }
                }
            }
            results
        }
    }

    override suspend fun getUpdateInterval(): Long {
        return getPluginConfig()?.minUpdateInterval ?: super.getUpdateInterval()
    }

    private fun getPluginConfig(): WeatherPluginConfig? {
        return PluginApi(pluginAuthority, context.contentResolver).getWeatherPluginConfig()
    }
}