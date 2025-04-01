package ir.mostafa.launcher.ui.launcher.search.favorites

import ir.mostafa.launcher.preferences.ui.UiState
import ir.mostafa.launcher.ui.common.FavoritesVM
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.inject

class SearchFavoritesVM : FavoritesVM() {
    private val uiState: UiState by inject()

    override val tagsExpanded: Flow<Boolean> = uiState.favoritesTagsExpanded

    override fun setTagsExpanded(expanded: Boolean) {
        uiState.setFavoritesTagsExpanded(expanded)
    }

}