package ir.mostafa.launcher.ui.launcher.widgets

import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.mostafa.launcher.ui.R
import ir.mostafa.launcher.ui.base.LocalAppWidgetHost
import ir.mostafa.launcher.ui.ktx.animateTo
import ir.mostafa.launcher.ui.launcher.sheets.LocalBottomSheetManager
import ir.mostafa.launcher.ui.launcher.sheets.WidgetPickerSheet
import ir.mostafa.launcher.widgets.AppWidget
import kotlinx.coroutines.launch

@Composable
fun WidgetColumn(
    modifier: Modifier = Modifier,
    editMode: Boolean = false,
    onEditModeChange: (Boolean) -> Unit,
) {

    val viewModel: WidgetsVM = viewModel()
    val bottomSheetManager = LocalBottomSheetManager.current

    var addNewWidget by rememberSaveable { mutableStateOf(false) }



    Column(
        modifier = modifier
    ) {
        val scope = rememberCoroutineScope()
        Column {
            val widgets by viewModel.widgets.collectAsState()
            val swapThresholds = remember(widgets) {
                Array(widgets.size) { floatArrayOf(0f, 0f) }
            }
            val widgetsWithIndex = remember(widgets) { widgets.withIndex() }
            for ((i, widget) in widgetsWithIndex) {
                key(widget.id) {
                    var dragOffsetAfterSwap = remember<Float?> { null }
                    val offsetY = remember(widgets) { mutableStateOf(dragOffsetAfterSwap ?: 0f) }

                    LaunchedEffect(widgets) {
                        dragOffsetAfterSwap = null
                    }

                    val widgetHost = LocalAppWidgetHost.current

                    WidgetItem(
                        widget = widget,
                        editMode = editMode,
                        onWidgetAdd = { widget, offset ->
                            viewModel.addWidget(widget, i + offset)
                        },
                        onWidgetRemove = {
                            if (widget is AppWidget) {
                                widgetHost.deleteAppWidgetId(widget.config.widgetId)
                            }
                            viewModel.removeWidget(widget)
                        },
                        onWidgetUpdate = {
                            viewModel.updateWidget(it)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onPlaced {
                                swapThresholds[i][0] = it.positionInParent().y
                                swapThresholds[i][1] = it.positionInParent().y + it.size.height
                            }
                            .padding(top = 8.dp)
                            .offset {
                                IntOffset(0, offsetY.value.toInt())
                            },
                        draggableState = rememberDraggableState {
                            scope.launch {
                                val newOffset = offsetY.value + it
                                offsetY.value = newOffset
                                if (i > 0 && newOffset < (swapThresholds[i - 1][0] - swapThresholds[i - 1][1])) {
                                    if (dragOffsetAfterSwap == null) {
                                        dragOffsetAfterSwap =
                                            swapThresholds[i - 1][1] - swapThresholds[i - 1][0] + newOffset
                                        viewModel.moveUp(i)
                                    }
                                }
                                if (i < widgets.lastIndex && newOffset > (swapThresholds[i + 1][1] - swapThresholds[i + 1][0])) {
                                    if (dragOffsetAfterSwap == null) {
                                        dragOffsetAfterSwap =
                                            swapThresholds[i + 1][0] - swapThresholds[i + 1][1] + newOffset
                                        viewModel.moveDown(i)
                                    }
                                }
                            }
                        },
                        onDragStopped = {
                            scope.launch {
                                offsetY.animateTo(0f)
                            }
                        }
                    )
                }
            }
        }

        val editButton by viewModel.editButton.collectAsState()
        if (editMode || editButton == true) {
            val title = stringResource(
                if (editMode) R.string.widget_add_widget
                else R.string.menu_edit_widgets
            )
            val icon =
                AnimatedImageVector.animatedVectorResource(R.drawable.anim_ic_edit_add)
            ExtendedFloatingActionButton(
                modifier = Modifier
                    .semantics {
                        role = Role.Button
                        contentDescription = title
                    }
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally),
                icon = {
                    Icon(
                        painter = rememberAnimatedVectorPainter(
                            animatedImageVector = icon,
                            atEnd = !editMode
                        ), contentDescription = null
                    )
                },
                text = {
                    Text(title)
                }, onClick = {
                    if (!editMode) {
                        onEditModeChange(true)
                    } else {
                        addNewWidget = true
                    }
                })

        }
    }

    if (addNewWidget) {
        WidgetPickerSheet(
            onDismiss = { addNewWidget = false },
            onWidgetSelected = {
                viewModel.addWidget(it)
                addNewWidget = false
            }
        )
    }
}