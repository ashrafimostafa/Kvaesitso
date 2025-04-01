package ir.mostafa.launcher.preferences

import android.content.Context
import ir.mostafa.launcher.preferences.migrations.Migration2
import ir.mostafa.launcher.preferences.migrations.Migration3
import ir.mostafa.launcher.settings.BaseSettings

internal class LauncherDataStore(
    private val context: Context,
): BaseSettings<LauncherSettingsData>(
    context,
    fileName = "settings.json",
    serializer = LauncherSettingsDataSerializer,
    migrations = listOf(
        Migration2(),
        Migration3(),
    ),
) {

    val data
        get() = context.dataStore.data

    fun update(block: (LauncherSettingsData) -> LauncherSettingsData) {
        updateData(block)
    }
}