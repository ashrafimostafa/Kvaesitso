package ir.mostafa.launcher.ui.settings.wikipedia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.mostafa.launcher.preferences.search.WikipediaSearchSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WikipediaSettingsScreenVM: ViewModel(), KoinComponent {
    private val wikipediaSearchSettings: WikipediaSearchSettings by inject()

    val wikipedia = wikipediaSearchSettings.enabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
    fun setWikipedia(wikipedia: Boolean) {
        wikipediaSearchSettings.setEnabled(wikipedia)
    }

    val customUrl = wikipediaSearchSettings.customUrl
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")
    fun setCustomUrl(customUrl: String) {
        wikipediaSearchSettings.setCustomUrl(customUrl)
    }
}