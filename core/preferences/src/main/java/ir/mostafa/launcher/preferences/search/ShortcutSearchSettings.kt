package ir.mostafa.launcher.preferences.search

import ir.mostafa.launcher.preferences.LauncherDataStore
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class ShortcutSearchSettings internal constructor(
    private val dataStore: LauncherDataStore
) {
    val enabled
        get() = dataStore.data.map { it.shortcutSearchEnabled }.distinctUntilChanged()

    fun setEnabled(enabled: Boolean) {
        dataStore.update {
            it.copy(shortcutSearchEnabled = enabled)
        }
    }
}