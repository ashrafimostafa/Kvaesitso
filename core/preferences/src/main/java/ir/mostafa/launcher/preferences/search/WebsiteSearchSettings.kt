package ir.mostafa.launcher.preferences.search

import ir.mostafa.launcher.preferences.LauncherDataStore
import kotlinx.coroutines.flow.map

class WebsiteSearchSettings internal constructor(
    private val dataStore: LauncherDataStore,
){
    val enabled
        get() = dataStore.data.map { it.websiteSearchEnabled }

    fun setEnabled(enabled: Boolean) {
        dataStore.update {
            it.copy(websiteSearchEnabled = enabled)
        }
    }
}