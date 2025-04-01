package ir.mostafa.launcher.calendar

import ir.mostafa.launcher.calendar.providers.AndroidCalendarEvent
import ir.mostafa.launcher.calendar.providers.PluginCalendarEvent
import ir.mostafa.launcher.search.CalendarEvent
import ir.mostafa.launcher.search.SearchableDeserializer
import ir.mostafa.launcher.search.SearchableRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val calendarModule = module {
    factory<SearchableRepository<CalendarEvent>>(named<CalendarEvent>()) { get<CalendarRepository>() }
    factory<CalendarRepository> { CalendarRepositoryImpl(androidContext(), get(), get(), get()) }
    factory<SearchableDeserializer>(named(AndroidCalendarEvent.Domain)) { AndroidCalendarEventDeserializer(androidContext()) }
    factory<SearchableDeserializer>(named(PluginCalendarEvent.Domain)) { PluginCalendarEventDeserializer(androidContext(), get()) }
}