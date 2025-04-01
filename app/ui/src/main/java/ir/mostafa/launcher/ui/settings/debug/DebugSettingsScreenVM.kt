package ir.mostafa.launcher.ui.settings.debug

import androidx.lifecycle.ViewModel
import ir.mostafa.launcher.data.customattrs.CustomAttributesRepository
import ir.mostafa.launcher.icons.IconService
import ir.mostafa.launcher.searchable.SavableSearchableRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DebugSettingsScreenVM: ViewModel(), KoinComponent {

    private val searchableRepository: SavableSearchableRepository by inject()
    private val customAttributesRepository: CustomAttributesRepository by inject()
    private val iconService: IconService by inject()
    suspend fun cleanUpDatabase(): Int {
        var removed = searchableRepository.cleanupDatabase()
        removed += customAttributesRepository.cleanupDatabase()
        return removed
    }

    fun reinstallIconPacks() {
        iconService.reinstallAllIconPacks()
    }
}