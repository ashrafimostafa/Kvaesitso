package ir.mostafa.launcher.ui.launcher.widgets

import android.appwidget.AppWidgetManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DragIndicator
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import ir.mostafa.launcher.ui.R
import ir.mostafa.launcher.ui.component.LauncherCard
import ir.mostafa.launcher.ui.launcher.sheets.ConfigureWidgetSheet
import ir.mostafa.launcher.ui.launcher.widgets.calendar.CalendarWidget
import ir.mostafa.launcher.ui.launcher.widgets.external.AppWidget
import ir.mostafa.launcher.ui.launcher.widgets.favorites.FavoritesWidget
import ir.mostafa.launcher.ui.launcher.widgets.music.MusicWidget
import ir.mostafa.launcher.ui.launcher.widgets.notes.NotesWidget
import ir.mostafa.launcher.ui.launcher.widgets.weather.WeatherWidget
import ir.mostafa.launcher.ui.locals.LocalCardStyle
import ir.mostafa.launcher.widgets.AppWidget
import ir.mostafa.launcher.widgets.CalendarWidget
import ir.mostafa.launcher.widgets.FavoritesWidget
import ir.mostafa.launcher.widgets.MusicWidget
import ir.mostafa.launcher.widgets.NotesWidget
import ir.mostafa.launcher.widgets.WeatherWidget
import ir.mostafa.launcher.widgets.Widget

@Composable
fun WidgetItem(
    widget: Widget,
    modifier: Modifier = Modifier,
    editMode: Boolean = false,
    onWidgetAdd: (widget: Widget, offset: Int) -> Unit = { _, _ -> },
    onWidgetUpdate: (widget: Widget) -> Unit = {},
    onWidgetRemove: () -> Unit = {},
    draggableState: DraggableState = rememberDraggableState {},
    onDragStopped: () -> Unit = {}
) {
    val context = LocalContext.current

    var configure by rememberSaveable { mutableStateOf(false) }

    var isDragged by remember { mutableStateOf(false) }
    val elevation by animateDpAsState(if (isDragged) 8.dp else 2.dp)

    val appWidget = if (widget is AppWidget) remember(widget.config.widgetId) {
        AppWidgetManager.getInstance(context).getAppWidgetInfo(widget.config.widgetId)
    } else null

    val backgroundOpacity by animateFloatAsState(
        if (widget is AppWidget && !widget.config.background && !editMode) 0f else LocalCardStyle.current.opacity,
        label = "widgetCardBackgroundOpacity",
    )

    LauncherCard(
        modifier = modifier.zIndex(if (isDragged) 1f else 0f),
        elevation = elevation,
        backgroundOpacity = backgroundOpacity,
    ) {
        Column {
            AnimatedVisibility(editMode) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DragIndicator,
                        contentDescription = null,
                        modifier = Modifier.draggable(
                            state = draggableState,
                            orientation = Orientation.Vertical,
                            startDragImmediately = true,
                            onDragStarted = {
                                isDragged = true
                            },
                            onDragStopped = {
                                isDragged = false
                                onDragStopped()
                            }
                        )
                    )
                    Text(
                        text = when (widget) {
                            is WeatherWidget -> stringResource(R.string.widget_name_weather)
                            is MusicWidget -> stringResource(R.string.widget_name_music)
                            is CalendarWidget -> stringResource(R.string.widget_name_calendar)
                            is FavoritesWidget -> stringResource(R.string.widget_name_favorites)
                            is NotesWidget -> stringResource(R.string.widget_name_notes)
                            is AppWidget -> remember(widget.config.widgetId) {
                                appWidget?.loadLabel(
                                    context.packageManager
                                )
                            }
                                ?: stringResource(R.string.widget_name_unknown)
                        },
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                    IconButton(onClick = {
                        configure = true
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.Tune,
                            contentDescription = stringResource(R.string.settings)
                        )
                    }
                    IconButton(onClick = { onWidgetRemove() }) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = stringResource(R.string.widget_action_remove)
                        )
                    }
                }
            }
            AnimatedVisibility(!editMode) {
                when (widget) {
                    is WeatherWidget -> {
                        WeatherWidget(widget)
                    }

                    is MusicWidget -> {
                        MusicWidget(widget)
                    }

                    is CalendarWidget -> {
                        CalendarWidget(widget)
                    }

                    is FavoritesWidget -> {
                        FavoritesWidget(widget)
                    }

                    is NotesWidget -> {
                        NotesWidget(
                            widget,
                            onWidgetAdd = onWidgetAdd,
                        )
                    }

                    is AppWidget -> {
                        AppWidget(
                            widget,
                            onWidgetUpdate = onWidgetUpdate,
                            onWidgetRemove = onWidgetRemove,
                        )
                    }
                }
            }
        }
    }
    if (configure) {
        ConfigureWidgetSheet(
            widget = widget,
            onWidgetUpdated = onWidgetUpdate,
            onDismiss = { configure = false },
        )
    }
}
