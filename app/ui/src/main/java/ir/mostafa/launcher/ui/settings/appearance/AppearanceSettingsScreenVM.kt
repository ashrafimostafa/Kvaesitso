package ir.mostafa.launcher.ui.settings.appearance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.mostafa.launcher.preferences.ColorScheme
import ir.mostafa.launcher.preferences.Font
import ir.mostafa.launcher.preferences.ui.UiSettings
import ir.mostafa.launcher.themes.ThemeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppearanceSettingsScreenVM : ViewModel(), KoinComponent {
    private val uiSettings: UiSettings by inject()

    private val themeRepository: ThemeRepository by inject()

    val colorScheme = uiSettings.colorScheme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun setColorScheme(colorScheme: ColorScheme) {
        uiSettings.setColorScheme(colorScheme)
    }

    val themeName = uiSettings.theme.flatMapLatest {
            themeRepository.getThemeOrDefault(it)
        }.map {
            it.name
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val compatModeColors = uiSettings.compatModeColors
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    fun setCompatModeColors(enabled: Boolean) {
        uiSettings.setCompatModeColors(enabled)
    }

    val font = uiSettings.font
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun setFont(font: Font) {
        uiSettings.setFont(font)
    }
}