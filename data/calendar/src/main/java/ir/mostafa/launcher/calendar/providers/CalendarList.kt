package ir.mostafa.launcher.calendar.providers

import ir.mostafa.launcher.search.calendar.CalendarListType

data class CalendarList(
    val id: String,
    val name: String,
    val owner: String?,
    val color: Int,
    val types: List<CalendarListType>,
    val providerId: String,
)