package ir.mostafa.launcher.ui.launcher.widgets.clock.parts

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import ir.mostafa.launcher.preferences.ui.UiSettings
import ir.mostafa.launcher.searchable.PinnedLevel
import ir.mostafa.launcher.services.favorites.FavoritesService
import ir.mostafa.launcher.ui.launcher.search.common.grid.SearchResultGrid
import ir.mostafa.launcher.widgets.CalendarWidget
import ir.mostafa.launcher.widgets.WidgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FavoritesPartProvider : PartProvider, KoinComponent {

    private val favoritesService: FavoritesService by inject()
    private val widgetRepository: WidgetRepository by inject()
    private val uiSettings: UiSettings by inject()

    override fun getRanking(context: Context): Flow<Int> = flow {
        emit(Int.MAX_VALUE)
    }

    @Composable
    override fun Component(compactLayout: Boolean) {
        val columns by remember {
            uiSettings.gridSettings.map {
                it.columnCount
            }
        }.collectAsState(0)
        val excludeCalendar by remember { widgetRepository.exists(CalendarWidget.Type) }.collectAsState(
            true
        )

        val favorites by remember(columns, excludeCalendar) {
            favoritesService.getFavorites(
                excludeTypes = if (excludeCalendar) listOf("calendar", "tag") else listOf("tag"),
                minPinnedLevel = PinnedLevel.FrequentlyUsed,
                limit = columns
            )
        }.collectAsState(emptyList())


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            SearchResultGrid(
                items = favorites,
                showLabels = false,
                columns = columns.coerceAtMost(favorites.size),
                transitionKey = null,
            )
        }
    }
}