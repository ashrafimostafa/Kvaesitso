package ir.mostafa.launcher.widgets

import ir.mostafa.launcher.database.entities.PartialWidgetEntity
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID


@Serializable
data class WeatherWidgetConfig(
    val showForecast: Boolean = true,
)

data class WeatherWidget(
    override val id: UUID,
    val config: WeatherWidgetConfig = WeatherWidgetConfig(),
) : Widget() {

    override fun toDatabaseEntity(): PartialWidgetEntity {
        return PartialWidgetEntity(
            id = id,
            type = Type,
            config = Json.encodeToString(config),
        )
    }

    companion object {
        const val Type = "weather"
    }
}