package ir.mostafa.launcher.ui.settings.easteregg

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.mostafa.launcher.preferences.IconShape
import ir.mostafa.launcher.preferences.ui.UiSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class EasterEggSettingsScreenVM: ViewModel(), KoinComponent {
    private val settings: UiSettings by inject()

    val easterEgg = settings.iconShape.map { it == IconShape.EasterEgg }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)
    fun setEasterEgg(easterEgg: Boolean) {
        settings.setIconShape(if (easterEgg) IconShape.EasterEgg else IconShape.PlatformDefault)
    }
}