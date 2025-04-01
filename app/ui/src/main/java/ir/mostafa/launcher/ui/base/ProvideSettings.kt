package ir.mostafa.launcher.ui.base

import androidx.compose.runtime.*
import ir.mostafa.launcher.preferences.IconShape
import ir.mostafa.launcher.preferences.ui.CardStyle
import ir.mostafa.launcher.preferences.ui.GridSettings
import ir.mostafa.launcher.preferences.ui.UiSettings
import ir.mostafa.launcher.ui.component.ProvideIconShape
import ir.mostafa.launcher.ui.locals.LocalCardStyle
import ir.mostafa.launcher.ui.locals.LocalFavoritesEnabled
import ir.mostafa.launcher.ui.locals.LocalGridSettings
import ir.mostafa.launcher.widgets.FavoritesWidget
import ir.mostafa.launcher.widgets.WidgetRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.androidx.compose.inject

@Composable
fun ProvideSettings(
    content: @Composable () -> Unit
) {
    val settings: UiSettings by inject()
    val widgetRepository: WidgetRepository by inject()

    val cardStyle by remember {
        settings.cardStyle.distinctUntilChanged()
    }.collectAsState(
        CardStyle()
    )
    val iconShape by remember {
        settings.iconShape.distinctUntilChanged()
    }.collectAsState(IconShape.Circle)

    val favoritesEnabled by remember {
        combine(
            widgetRepository.exists(FavoritesWidget.Type),
            settings.favoritesEnabled,
        ) { a, b -> a || b }.distinctUntilChanged()
    }.collectAsState(true)

    val gridSettings by remember {
        settings.gridSettings.distinctUntilChanged()
    }.collectAsState(GridSettings())

    CompositionLocalProvider(
        LocalCardStyle provides cardStyle,
        LocalFavoritesEnabled provides favoritesEnabled,
        LocalGridSettings provides gridSettings,
    ) {
        ProvideIconShape(iconShape) {
            content()
        }
    }

}