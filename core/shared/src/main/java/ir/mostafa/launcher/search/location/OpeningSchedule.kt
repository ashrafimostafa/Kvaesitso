package ir.mostafa.launcher.search.location

import ir.mostafa.launcher.serialization.DurationSerializer
import ir.mostafa.launcher.serialization.LocalTimeSerializer
import ir.mostafa.launcher.serialization.OpeningScheduleSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalTime

@Serializable
data class OpeningHours(
    @JsonNames("day")
    val dayOfWeek: DayOfWeek,
    @JsonNames("openingTime")
    @Serializable(with = LocalTimeSerializer::class)
    val startTime: LocalTime,
    @Serializable(with = DurationSerializer::class)
    val duration: Duration
) {
    override fun toString(): String = "$dayOfWeek $startTime-${startTime.plus(duration)}"
}

@Serializable(with = OpeningScheduleSerializer::class)
sealed interface OpeningSchedule {
    data object TwentyFourSeven : OpeningSchedule
    @Serializable
    data class Hours(@Serializable val openingHours: List<OpeningHours>) : OpeningSchedule
}