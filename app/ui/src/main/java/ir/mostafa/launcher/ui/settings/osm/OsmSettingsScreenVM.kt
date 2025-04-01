package ir.mostafa.launcher.ui.settings.osm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.mostafa.launcher.plugins.PluginService
import ir.mostafa.launcher.preferences.search.LocationSearchSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class OsmSettingsScreenVM: ViewModel(), KoinComponent {
    private val settings: LocationSearchSettings by inject()
    private val pluginService: PluginService by inject()

    val osmLocations = settings.osmLocations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun setOsmLocations(osmLocations: Boolean) {
        settings.setOsmLocations(osmLocations)
    }

    val hideUncategorized = settings.hideUncategorized
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun setHideUncategorized(hideUncategorized: Boolean) {
        settings.setHideUncategorized(hideUncategorized)
    }

    val customOverpassUrl = settings.overpassUrl
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")

    fun setCustomOverpassUrl(customUrl: String) {
        settings.setOverpassUrl(customUrl)
    }
}