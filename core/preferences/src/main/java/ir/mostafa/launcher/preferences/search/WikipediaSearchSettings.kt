package ir.mostafa.launcher.preferences.search

import ir.mostafa.launcher.preferences.LauncherDataStore
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class WikipediaSearchSettings internal constructor(
    private val dataStore: LauncherDataStore
) {
    val enabled
        get() = dataStore.data.map { it.wikipediaSearchEnabled }.distinctUntilChanged()

    fun setEnabled(enabled: Boolean) {
        dataStore.update {
            it.copy(wikipediaSearchEnabled = enabled)
        }
    }

    val customUrl
        get() = dataStore.data.map { it.wikipediaCustomUrl }.distinctUntilChanged()

    fun setCustomUrl(customUrl: String?) {
        dataStore.update {
            it.copy(wikipediaCustomUrl = customUrl?.takeIf { it.isNotBlank() })
        }
    }
}