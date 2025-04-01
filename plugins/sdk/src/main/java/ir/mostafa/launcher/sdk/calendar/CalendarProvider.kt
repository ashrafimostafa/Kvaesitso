package ir.mostafa.launcher.sdk.calendar

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.CancellationSignal
import ir.mostafa.launcher.plugin.PluginType
import ir.mostafa.launcher.plugin.config.QueryPluginConfig
import ir.mostafa.launcher.plugin.contracts.CalendarPluginContract
import ir.mostafa.launcher.plugin.contracts.CalendarPluginContract.CalendarListColumns
import ir.mostafa.launcher.plugin.contracts.CalendarPluginContract.EventColumns
import ir.mostafa.launcher.plugin.data.buildCursor
import ir.mostafa.launcher.plugin.data.get
import ir.mostafa.launcher.sdk.base.QueryPluginProvider
import ir.mostafa.launcher.sdk.utils.launchWithCancellationSignal
import ir.mostafa.launcher.search.calendar.CalendarQuery

abstract class CalendarProvider(
    config: QueryPluginConfig,
) : QueryPluginProvider<CalendarQuery, CalendarEvent>(
    config
) {
    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        queryArgs: Bundle?,
        cancellationSignal: CancellationSignal?
    ): Cursor? {
        super.query(uri, projection, queryArgs, cancellationSignal)?.let {
            return it
        }

        if (uri.pathSegments.size == 1 && uri.pathSegments.first() == CalendarPluginContract.Paths.CalendarLists) {
            return getCalendarLists(cancellationSignal).toCursor()
        }
        return null
    }

    private fun getCalendarLists(cancellationSignal: CancellationSignal?): List<CalendarList> {
        return launchWithCancellationSignal(cancellationSignal) {
            getCalendarLists()
        }
    }

    abstract suspend fun getCalendarLists(): List<CalendarList>

    override fun getQuery(uri: Uri): CalendarQuery {
        val query = uri.getQueryParameter(CalendarPluginContract.Params.Query)
        val start = uri.getQueryParameter(CalendarPluginContract.Params.StartTime)?.toLongOrNull()
        val end = uri.getQueryParameter(CalendarPluginContract.Params.EndTime)?.toLongOrNull()
        val excludedCalendars = uri.getQueryParameter(CalendarPluginContract.Params.Exclude)?.split(",") ?: emptyList()

        return CalendarQuery(
            query = query,
            start = start,
            end = end,
            excludedCalendars = excludedCalendars,
        )
    }

    override fun Bundle.toResult(): CalendarEvent? {
        return CalendarEvent(
            id = get(EventColumns.Id) ?: return null,
            title = get(EventColumns.Title) ?: return null,
            description = get(EventColumns.Description),
            location = get(EventColumns.Location),
            color = get(EventColumns.Color),
            calendarName = get(EventColumns.CalendarName),
            startTime = get(EventColumns.StartTime),
            endTime = get(EventColumns.EndTime) ?: return null,
            includeTime = get(EventColumns.IncludeTime) ?: true,
            attendees = get(EventColumns.Attendees) ?: emptyList(),
            uri = Uri.parse(get(EventColumns.Uri) ?: return null),
        )
    }

    override fun List<CalendarEvent>.toCursor(): Cursor {
        return buildCursor(EventColumns, this) {
            put(EventColumns.Id, it.id)
            put(EventColumns.Title, it.title)
            put(EventColumns.Description, it.description)
            put(EventColumns.Location, it.location)
            put(EventColumns.CalendarName, it.calendarName)
            put(EventColumns.Color, it.color)
            put(EventColumns.StartTime, it.startTime)
            put(EventColumns.EndTime, it.endTime)
            put(EventColumns.IncludeTime, it.includeTime)
            put(EventColumns.Attendees, it.attendees)
            put(EventColumns.Uri, it.uri.toString())
            put(EventColumns.IsCompleted, it.isCompleted)
        }
    }

    override fun getPluginType(): PluginType {
        return PluginType.Calendar
    }

    private fun List<CalendarList>.toCursor(): Cursor {
        return buildCursor(CalendarListColumns, this) {
            put(CalendarListColumns.Id, it.id)
            put(CalendarListColumns.Name, it.name)
            put(CalendarListColumns.Color, it.color)
            put(CalendarListColumns.AccountName, it.accountName)
            put(CalendarListColumns.ContentTypes, it.contentTypes)
        }
    }
}