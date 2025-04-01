package ir.mostafa.launcher.widgets

import ir.mostafa.launcher.database.entities.PartialWidgetEntity
import ir.mostafa.launcher.database.entities.WidgetEntity
import ir.mostafa.launcher.ktx.decodeFromStringOrNull
import kotlinx.serialization.json.Json
import java.util.UUID

sealed class Widget {

    abstract val id: UUID
    internal fun toDatabaseEntity(position: Int, parentId: UUID? = null): WidgetEntity {
        return toDatabaseEntity().let {
            WidgetEntity(
                id = it.id,
                type = it.type,
                config = it.config,
                position = position,
                parentId = parentId,
            )
        }
    }

    abstract fun toDatabaseEntity(): PartialWidgetEntity

    companion object {
        fun fromDatabaseEntity(entity: WidgetEntity): Widget? {
            return when (entity.type) {
                WeatherWidget.Type -> {
                    val config: WeatherWidgetConfig =
                        Json.decodeFromStringOrNull(entity.config?.takeIf { it.isNotBlank() })
                            ?: WeatherWidgetConfig()
                    WeatherWidget(entity.id, config)
                }
                MusicWidget.Type -> MusicWidget(entity.id)
                CalendarWidget.Type -> {
                    val config: CalendarWidgetConfig =
                        Json.decodeFromStringOrNull(entity.config?.takeIf { it.isNotBlank() })
                            ?: CalendarWidgetConfig()
                    CalendarWidget(entity.id, config)
                }
                FavoritesWidget.Type -> {
                    val config: FavoritesWidgetConfig =
                        Json.decodeFromStringOrNull(entity.config?.takeIf { it.isNotBlank() })
                            ?: FavoritesWidgetConfig()
                    FavoritesWidget(entity.id, config)
                }
                AppWidget.Type -> {
                    val config: AppWidgetConfig =
                        Json.decodeFromStringOrNull(entity.config?.takeIf { it.isNotBlank() })
                            ?: return null
                    AppWidget(
                        entity.id,
                        config,
                    )
                }
                NotesWidget.Type -> {
                    val config: NotesWidgetConfig =
                        Json.decodeFromStringOrNull(entity.config?.takeIf { it.isNotBlank() })
                            ?: NotesWidgetConfig()
                    NotesWidget(entity.id, config)
                }

                else -> null
            }
        }
    }
}


data class MusicWidget(
    override val id: UUID,
) : Widget() {
    override fun toDatabaseEntity(): PartialWidgetEntity {
        return PartialWidgetEntity(
            id = id,
            type = Type,
            config = null
        )
    }

    companion object {
        const val Type = "music"
    }
}



enum class WidgetType(val value: String) {
    INTERNAL("internal"),
    THIRD_PARTY("3rdparty")
}