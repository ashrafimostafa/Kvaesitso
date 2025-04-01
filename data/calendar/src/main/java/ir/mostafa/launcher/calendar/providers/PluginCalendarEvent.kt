package ir.mostafa.launcher.calendar.providers

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import ir.mostafa.launcher.calendar.PluginCalendarEventSerializer
import ir.mostafa.launcher.ktx.tryStartActivity
import ir.mostafa.launcher.plugin.config.StorageStrategy
import ir.mostafa.launcher.search.CalendarEvent
import ir.mostafa.launcher.search.SavableSearchable
import ir.mostafa.launcher.search.SearchableSerializer
import ir.mostafa.launcher.search.UpdatableSearchable
import ir.mostafa.launcher.search.UpdateResult

data class PluginCalendarEvent(
    val id: String,
    val authority: String,
    override val color: Int?,
    override val startTime: Long?,
    override val endTime: Long,
    override val allDay: Boolean,
    override val description: String?,
    override val calendarName: String?,
    override val location: String?,
    override val attendees: List<String>,
    val uri: Uri,
    override val isCompleted: Boolean?,
    override val label: String,
    override val labelOverride: String? = null,
    override val timestamp: Long,
    internal val storageStrategy: StorageStrategy,
    override val updatedSelf: (suspend (SavableSearchable) -> UpdateResult<CalendarEvent>)?,
) : CalendarEvent, UpdatableSearchable<CalendarEvent> {
    override val domain: String = Domain

    override val key: String
        get() = "$domain://$authority:$id"

    override fun overrideLabel(label: String): SavableSearchable {
        return copy(
            labelOverride = label
        )
    }

    override fun launch(context: Context, options: Bundle?): Boolean {
        return context.tryStartActivity(
            Intent(
                Intent.ACTION_VIEW
            ).apply {
                data = uri
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }, options
        )
    }

    override fun getSerializer(): SearchableSerializer {
        return PluginCalendarEventSerializer()
    }

    companion object {
        const val Domain = "plugin.calendar"
    }
}