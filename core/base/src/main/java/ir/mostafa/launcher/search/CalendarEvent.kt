package ir.mostafa.launcher.search

import android.content.Context
import ir.mostafa.launcher.icons.ColorLayer
import ir.mostafa.launcher.icons.StaticLauncherIcon
import ir.mostafa.launcher.icons.TextLayer
import java.text.SimpleDateFormat

interface CalendarEvent : SavableSearchable {
    val color: Int?
    val startTime: Long?
    val endTime: Long
    val allDay: Boolean
    val description: String?
    val calendarName: String?
    val location: String?
    val attendees: List<String>
    val isCompleted: Boolean?
        get() = null


    override val preferDetailsOverLaunch: Boolean
        get() = true


    override fun getPlaceholderIcon(context: Context): StaticLauncherIcon {
        val df = SimpleDateFormat("dd")
        return StaticLauncherIcon(
            foregroundLayer = TextLayer(
                text = df.format(startTime ?: endTime),
                color = color ?: 0,
            ),
            backgroundLayer = ColorLayer(color ?: 0)
        )
    }

    fun openLocation(context: Context) {}
}