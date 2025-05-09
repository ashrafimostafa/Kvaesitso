package ir.mostafa.launcher.ui.launcher.widgets.favorites

import ir.mostafa.launcher.preferences.ui.GridSettings
import ir.mostafa.launcher.preferences.ui.UiSettings
import ir.mostafa.launcher.services.widgets.WidgetsService
import ir.mostafa.launcher.ui.common.FavoritesVM
import ir.mostafa.launcher.widgets.FavoritesWidget
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.koin.core.component.inject

class FavoritesWidgetVM : FavoritesVM() {

    private val widgetsService: WidgetsService by inject()

    private val uiSettings: UiSettings by inject()

    private val widget = MutableStateFlow<FavoritesWidget?>(null)
    override val tagsExpanded = widget.map { it?.config?.tagsMultiline == true }

    private val isTopWidget = widgetsService.isFavoritesWidgetFirst()
    private val clockWidgetFavSlots =
        combine(uiSettings.dock, isTopWidget, uiSettings.gridSettings) { (dock, isTop, grid) ->
            dock as Boolean
            isTop as Boolean
            grid as GridSettings
            if (!isTop || !dock) 0
            else {
                grid.columnCount
            }
        }

    override val favorites = super.favorites.combine(clockWidgetFavSlots) { favs, slots ->
        if (selectedTag.value == null) {
            if (favs.lastIndex < slots) emptyList()
            else favs.subList(slots, favs.size)
        } else {
            favs
        }
    }

    override fun setTagsExpanded(expanded: Boolean) {
        val widget = this.widget.value ?: return
        widgetsService.updateWidget(
            widget.copy(
                config = widget.config.copy(tagsMultiline = expanded)
            )
        )
    }

    fun updateWidget(widget: FavoritesWidget) {
        this.widget.value = widget
    }

}