package ir.mostafa.launcher.preferences.search

import ir.mostafa.launcher.preferences.LauncherDataStore
import kotlinx.coroutines.flow.map

class CalendarSearchSettings internal constructor(
    private val dataStore: LauncherDataStore,
) {
    val providers
        get() = dataStore.data.map { it.calendarSearchProviders }

    val enabledProviders
        get() = dataStore.data.map { it.calendarSearchProviders }

    fun setProviderEnabled(provider: String, enabled: Boolean) {
        dataStore.update {
            if (enabled) {
                it.copy(calendarSearchProviders = it.calendarSearchProviders + provider)
            } else {
                it.copy(calendarSearchProviders = it.calendarSearchProviders - provider)
            }
        }
    }

    val excludedCalendars
        get() = dataStore.data.map { it.calendarSearchExcludedCalendars }
    fun setCalendarExcluded(calendarId: String, excluded: Boolean) {
        dataStore.update {
            if (excluded) {
                it.copy(calendarSearchExcludedCalendars = it.calendarSearchExcludedCalendars + calendarId)
            } else {
                it.copy(calendarSearchExcludedCalendars = it.calendarSearchExcludedCalendars - calendarId)
            }
        }
    }
}