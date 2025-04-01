package ir.mostafa.launcher.search.location

import android.graphics.Color
import ir.mostafa.launcher.serialization.ColorSerializer
import ir.mostafa.launcher.serialization.DurationSerializer
import ir.mostafa.launcher.serialization.ZonedDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.Duration
import java.time.ZonedDateTime

@Serializable
data class Departure(
    /**
     * The scheduled time of the departure.
     */
    @Serializable(with = ZonedDateTimeSerializer::class)
    val time: ZonedDateTime,
    /**
     * The delay of the departure.
     * `Duration.ZERO` if the departure is on time,
     * `null` if no real-time data is available.
     */
    @Serializable(with = DurationSerializer::class)
    val delay: Duration?,
    /**
     * Name of the line (i.e. "11", "U2", "S1").
     */
    val line: String,
    val lastStop: String?,
    val type: LineType? = null,
    @Serializable(with = ColorSerializer::class)
    val lineColor: Color?,
)

enum class LineType {
    Bus,
    Tram,
    Subway,
    CommuterTrain,
    RegionalTrain,
    Train,
    HighSpeedTrain,
    Boat,
    Monorail,
    CableCar,
    Airplane,
}