package ir.mostafa.launcher.calendar.providers

import ir.mostafa.launcher.search.CalendarEvent

internal interface CalendarProvider {
    suspend fun search(
        query: String? = null,
        from: Long = System.currentTimeMillis(),
        to: Long = from + 14 * 24 * 60 * 60 * 1000L,
        excludedCalendars: List<String> = emptyList(),
        excludeAllDayEvents: Boolean = false,
        allowNetwork: Boolean = false,
    ): List<CalendarEvent>

    suspend fun getCalendarLists(): List<CalendarList>

    val namespace: String
}