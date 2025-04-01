package ir.mostafa.launcher.ui.launcher.widgets.external

import android.appwidget.AppWidgetManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ir.mostafa.launcher.ui.R
import ir.mostafa.launcher.ui.component.Banner
import ir.mostafa.launcher.ui.launcher.sheets.WidgetPickerSheet
import ir.mostafa.launcher.ui.locals.LocalDarkTheme
import ir.mostafa.launcher.ui.locals.LocalPreferDarkContentOverWallpaper
import ir.mostafa.launcher.widgets.AppWidget
import ir.mostafa.launcher.widgets.CalendarWidget
import ir.mostafa.launcher.widgets.FavoritesWidget
import ir.mostafa.launcher.widgets.MusicWidget
import ir.mostafa.launcher.widgets.NotesWidget
import ir.mostafa.launcher.widgets.WeatherWidget
import ir.mostafa.launcher.widgets.Widget

@Composable
fun AppWidget(
    widget: AppWidget,
    onWidgetUpdate: (Widget) -> Unit,
    onWidgetRemove: () -> Unit,
) {
    val context = LocalContext.current

    val lightBackground = (!LocalDarkTheme.current && widget.config.background) || LocalPreferDarkContentOverWallpaper.current

    val widgetInfo = remember(widget.config.widgetId) {
        AppWidgetManager.getInstance(context)
            .getAppWidgetInfo(widget.config.widgetId)
    }
    if (widgetInfo == null) {
        var replaceWidget by rememberSaveable {
            mutableStateOf(false)
        }
        Banner(
            modifier = Modifier.padding(16.dp),
            text = stringResource(R.string.app_widget_loading_failed),
            icon = Icons.Rounded.Warning,
            secondaryAction = {
                OutlinedButton(onClick = onWidgetRemove) {
                    Text(stringResource(R.string.widget_action_remove))
                }
            },
            primaryAction = {
                Button(onClick = { replaceWidget = true }) {
                    Text(stringResource(R.string.widget_action_replace))
                }
            }
        )
        if (replaceWidget) {
            WidgetPickerSheet(
                onDismiss = { replaceWidget = false },
                onWidgetSelected = {
                    val updatedWidget = when (it) {
                        is AppWidget -> widget.copy(
                            config = widget.config.copy(
                                widgetId = it.config.widgetId
                            )
                        )

                        is WeatherWidget -> it.copy(id = widget.id)
                        is MusicWidget -> it.copy(id = widget.id)
                        is CalendarWidget -> it.copy(id = widget.id)
                        is FavoritesWidget -> it.copy(id = widget.id)
                        is NotesWidget -> it.copy(id = widget.id)
                    }
                    onWidgetUpdate(updatedWidget)
                    replaceWidget = false
                }
            )
        }
    } else {
        val width = widget.config.width
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            AppWidgetHost(
                widgetId = widget.config.widgetId,
                widgetInfo = widgetInfo,
                modifier = Modifier
                    .then(
                        if (width == null) Modifier.fillMaxWidth() else Modifier.width(width.dp)
                    )
                    .height(widget.config.height.dp),
                borderless = widget.config.borderless,
                useThemeColors = widget.config.themeColors,
                onLightBackground = lightBackground,
            )
        }
    }
}