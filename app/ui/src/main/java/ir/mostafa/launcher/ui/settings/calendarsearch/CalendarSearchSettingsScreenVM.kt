package ir.mostafa.launcher.ui.settings.calendarsearch

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import ir.mostafa.launcher.calendar.CalendarRepository
import ir.mostafa.launcher.permissions.PermissionGroup
import ir.mostafa.launcher.permissions.PermissionsManager
import ir.mostafa.launcher.plugin.PluginType
import ir.mostafa.launcher.plugins.PluginService
import ir.mostafa.launcher.preferences.search.CalendarSearchSettings
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CalendarSearchSettingsScreenVM : ViewModel(), KoinComponent {
    private val settings: CalendarSearchSettings by inject()
    private val calendarRepository: CalendarRepository by inject()
    private val pluginService: PluginService by inject()
    private val permissionsManager: PermissionsManager by inject()

    val hasCalendarPermission = permissionsManager.hasPermission(PermissionGroup.Calendar)

    val availablePlugins = pluginService.getPluginsWithState(
        type = PluginType.Calendar,
        enabled = true,
    )

    val enabledProviders = settings.enabledProviders

    fun setProviderEnabled(providerId: String, enabled: Boolean) {
        settings.setProviderEnabled(providerId, enabled)
    }

    fun requestCalendarPermission(activity: AppCompatActivity) {
        permissionsManager.requestPermission(activity, PermissionGroup.Calendar)
    }

    val calendarLists = calendarRepository.getCalendars()

    val excludedCalendars = settings.excludedCalendars
    fun setCalendarExcluded(calendarId: String, excluded: Boolean) {
        settings.setCalendarExcluded(calendarId, excluded)
    }
}