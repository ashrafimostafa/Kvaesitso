package ir.mostafa.launcher.services.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.content.pm.LauncherApps
import androidx.core.content.getSystemService
import ir.mostafa.launcher.widgets.CalendarWidget
import ir.mostafa.launcher.widgets.FavoritesWidget
import ir.mostafa.launcher.widgets.MusicWidget
import ir.mostafa.launcher.widgets.NotesWidget
import ir.mostafa.launcher.widgets.WeatherWidget
import ir.mostafa.launcher.widgets.Widget
import ir.mostafa.launcher.widgets.WidgetRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID

class WidgetsService(
    private val context: Context,
    private val widgetRepository: WidgetRepository,
) {
    suspend fun getAppWidgetProviders(): List<AppWidgetProviderInfo> = withContext(Dispatchers.IO) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val launcherApps =
            context.getSystemService<LauncherApps>() ?: return@withContext emptyList()
        val profiles = launcherApps.profiles
        val widgets = mutableListOf<AppWidgetProviderInfo>()
        for (profile in profiles) {
            widgets.addAll(appWidgetManager.getInstalledProvidersForProfile(profile))
        }
        widgets
    }

    fun getAvailableBuiltInWidgets(): Flow<List<BuiltInWidgetInfo>> {
        return flowOf(getBuiltInWidgets())
    }

    fun getBuiltInWidgets(): List<BuiltInWidgetInfo> {
        return listOf(
            BuiltInWidgetInfo(
                type = WeatherWidget.Type,
                label = context.getString(R.string.widget_name_weather),
            ),
            BuiltInWidgetInfo(
                type = MusicWidget.Type,
                label = context.getString(R.string.widget_name_music),
            ),
            BuiltInWidgetInfo(
                type = CalendarWidget.Type,
                label = context.getString(R.string.widget_name_calendar),
            ),
            BuiltInWidgetInfo(
                type = FavoritesWidget.Type,
                label = context.getString(R.string.widget_name_favorites),
            ),
            BuiltInWidgetInfo(
                type = NotesWidget.Type,
                label = context.getString(R.string.widget_name_notes),
            ),
        )
    }

    fun addWidget(widget: Widget, position: Int, parentId: UUID? = null) {
        widgetRepository.create(widget, position, parentId)
    }

    fun updateWidget(widget: Widget) {
        widgetRepository.update(widget)
    }

    fun getWidgets() = widgetRepository.get()

    fun isFavoritesWidgetFirst(): Flow<Boolean> {
        return widgetRepository.get(limit = 1).map {
            it.firstOrNull() is FavoritesWidget
        }
    }

    fun countWidgets(type: String) = widgetRepository.count(type)

    fun removeWidget(widget: Widget) {
        widgetRepository.delete(widget)
    }

    companion object {
        const val AppWidgetHostId = 44203
    }
}