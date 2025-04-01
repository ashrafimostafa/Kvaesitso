package ir.mostafa.launcher.calendar

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.content.ContextCompat
import ir.mostafa.launcher.calendar.providers.AndroidCalendarEvent
import ir.mostafa.launcher.calendar.providers.AndroidCalendarProvider
import ir.mostafa.launcher.calendar.providers.PluginCalendarEvent
import ir.mostafa.launcher.calendar.providers.PluginCalendarProvider
import ir.mostafa.launcher.plugin.PluginRepository
import ir.mostafa.launcher.plugin.config.StorageStrategy
import ir.mostafa.launcher.search.SavableSearchable
import ir.mostafa.launcher.search.SearchableDeserializer
import ir.mostafa.launcher.search.SearchableSerializer
import ir.mostafa.launcher.search.UpdateResult
import ir.mostafa.launcher.search.asUpdateResult
import ir.mostafa.launcher.serialization.Json
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import org.json.JSONObject

class AndroidCalendarEventSerializer: SearchableSerializer {
    override fun serialize(searchable: SavableSearchable): String {
        searchable as AndroidCalendarEvent
        val json = JSONObject()
        json.put("id", searchable.id)
        return json.toString()
    }

    override val typePrefix: String
        get() = AndroidCalendarEvent.Domain
}

class AndroidCalendarEventDeserializer(val context: Context): SearchableDeserializer {
    override suspend fun deserialize(serialized: String): SavableSearchable? {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) return null
        val json = JSONObject(serialized)
        val id = json.getLong("id")
        return AndroidCalendarProvider(context).get(id)
    }

}

@Serializable
internal data class SerializedCalendarEvent(
    val id: String? = null,
    val authority: String? = null,
    val color: Int? = null,
    val startTime: Long? = null,
    val endTime: Long? = null,
    val allDay: Boolean? = null,
    val description: String? = null,
    val calendarName: String? = null,
    val location: String? = null,
    val attendees: List<String>? = null,
    val uri: String? = null,
    val completed: Boolean? = null,
    val label: String? = null,
    val timestamp: Long = 0L,
    val strategy: StorageStrategy = StorageStrategy.StoreCopy,
)

class PluginCalendarEventSerializer: SearchableSerializer {
    override fun serialize(searchable: SavableSearchable): String {
        searchable as PluginCalendarEvent
        if (searchable.storageStrategy == StorageStrategy.StoreCopy) {
            return Json.Lenient.encodeToString(
                SerializedCalendarEvent(
                    id = searchable.id,
                    authority = searchable.authority,
                    color = searchable.color,
                    startTime = searchable.startTime,
                    endTime = searchable.endTime,
                    allDay = searchable.allDay,
                    description = searchable.description,
                    calendarName = searchable.calendarName,
                    location = searchable.location,
                    attendees = searchable.attendees,
                    uri = searchable.uri.toString(),
                    completed = searchable.isCompleted,
                    label = searchable.label,
                    strategy = searchable.storageStrategy,
                    timestamp = searchable.timestamp,
                )
            )
        } else {
            return Json.Lenient.encodeToString(
                SerializedCalendarEvent(
                    id = searchable.id,
                    authority = searchable.authority,
                    strategy = searchable.storageStrategy,
                )
            )
        }
    }

    override val typePrefix: String
        get() = PluginCalendarEvent.Domain
}

class PluginCalendarEventDeserializer(
    val context: Context,
    private val pluginRepository: PluginRepository,
): SearchableDeserializer {
    override suspend fun deserialize(serialized: String): SavableSearchable? {
        val json = Json.Lenient.decodeFromString<SerializedCalendarEvent>(serialized)
        val authority = json.authority ?: return null
        val id = json.id ?: return null
        val strategy = json.strategy
        val plugin = pluginRepository.get(authority).firstOrNull() ?: return null
        if (!plugin.enabled) return null

        return when(strategy) {
            StorageStrategy.StoreReference -> {
                PluginCalendarProvider(context, authority).get(id).getOrNull()
            }
            else -> {
                val timestamp = json.timestamp
                PluginCalendarEvent(
                    id = id,
                    color = json.color,
                    startTime = json.startTime ?: 0,
                    endTime = json.endTime ?: 0,
                    allDay = json.allDay ?: false,
                    description = json.description,
                    calendarName = json.calendarName,
                    location = json.location,
                    attendees = json.attendees ?: emptyList(),
                    label = json.label ?: return null,
                    uri = Uri.parse(json.uri ?: return null),
                    isCompleted = json.completed,
                    storageStrategy = StorageStrategy.StoreCopy,
                    authority = authority,
                    timestamp = timestamp,
                    updatedSelf = {
                        if (it !is PluginCalendarEvent) UpdateResult.TemporarilyUnavailable()
                        else PluginCalendarProvider(context, authority).refresh(it, timestamp).asUpdateResult()
                    }
                )
            }
        }
    }

}