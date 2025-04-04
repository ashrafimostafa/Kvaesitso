package ir.mostafa.launcher.ui.launcher.widgets.notes

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.LinkOff
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.SaveAlt
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.mostafa.launcher.ui.R
import ir.mostafa.launcher.ui.component.Banner
import ir.mostafa.launcher.ui.component.BottomSheetDialog
import ir.mostafa.launcher.ui.component.markdown.MarkdownEditor
import ir.mostafa.launcher.ui.component.markdown.MarkdownText
import ir.mostafa.launcher.ui.locals.LocalSnackbarHostState
import ir.mostafa.launcher.widgets.NotesWidget
import ir.mostafa.launcher.widgets.NotesWidgetConfig
import ir.mostafa.launcher.widgets.Widget
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Composable
fun NotesWidget(
    widget: NotesWidget,
    onWidgetAdd: (widget: Widget, offset: Int) -> Unit,
) {
    val context = LocalContext.current
    val snackbarHostState = LocalSnackbarHostState.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val scope = rememberCoroutineScope()

    var showConflictResolveSheet by remember { mutableStateOf(false) }
    var readWriteErrorSheetText by remember { mutableStateOf<String?>(null) }

    var focused by remember { mutableStateOf(false) }

    val viewModel: NotesWidgetVM =
        viewModel(key = "notes-widget-${widget.id}", factory = NotesWidgetVM.Factory)

    val isLastWidget by viewModel.isLastNoteWidget.collectAsState(null)

    LaunchedEffect(widget) {
        viewModel.updateWidget(context, widget)
    }
    LaunchedEffect(Unit) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.onResume(context)
        }
    }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/markdown"),
        onResult = {
            if (it != null) viewModel.exportNote(context, it)
        }
    )

    val linkFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/markdown")
    ) {
        it ?: return@rememberLauncherForActivityResult
        viewModel.linkFile(context, it)
    }

    val text by viewModel.noteText
    if (viewModel.linkedFileConflict.value) {
        Banner(
            modifier = Modifier.padding(16.dp),
            text = stringResource(R.string.note_widget_conflict),
            icon = Icons.Rounded.ErrorOutline,
            primaryAction = {
                Button(onClick = { showConflictResolveSheet = true }) {
                    Text(stringResource(R.string.note_widget_conflict_action_resolve))
                }
            },
        )
        if (showConflictResolveSheet) {
            NoteWidgetConflictResolveSheet(
                localContent = widget.config.storedText,
                fileContent = text.text,
                onResolve = {
                    viewModel.resolveFileContentConflict(context, it)
                    showConflictResolveSheet = false
                },
                onDismissRequest = {
                    showConflictResolveSheet = false
                }
            )
        }
        return
    }
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .heightIn(min = 64.dp)
                .animateContentSize(),
        ) {
            MarkdownEditor(
                value = text,
                onValueChange = { viewModel.setText(context, it) },
                focus = focused,
                onFocusChange = { focused = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                placeholder = {
                    Text(
                        stringResource(R.string.notes_widget_placeholder),
                    )
                }
            )
            AnimatedVisibility(text.text.isBlank()) {
                Row(
                    modifier = Modifier.padding(8.dp),
                ) {
                    val tooltipState = rememberTooltipState()
                    TooltipBox(
                        state = tooltipState,
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = {
                            PlainTooltip {
                                Text(
                                    stringResource(
                                        if (widget.config.linkedFile == null) R.string.note_widget_link_file
                                        else R.string.note_widget_action_unlink_file
                                    )
                                )
                            }
                        }) {
                        IconButton(
                            onClick = {
                                if (widget.config.linkedFile == null) {
                                    linkFileLauncher.launch(
                                        getDefaultNoteFileName(context)
                                    )
                                } else {
                                    viewModel.unlinkFile(context)
                                }
                            },
                            modifier = Modifier.combinedClickable(
                                onClick = {},
                                onLongClick = {
                                    scope.launch {
                                        tooltipState.show()
                                    }
                                }
                            )
                        ) {
                            Icon(
                                if (widget.config.linkedFile == null) Icons.Rounded.Link
                                else Icons.Rounded.LinkOff,
                                stringResource(
                                    if (widget.config.linkedFile == null) R.string.note_widget_link_file
                                    else R.string.note_widget_action_unlink_file
                                )
                            )
                        }
                    }
                    if (isLastWidget == false) {
                        TooltipBox(
                            state = tooltipState,
                            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                            tooltip = {
                                PlainTooltip {
                                    Text(stringResource(R.string.notes_widget_action_dismiss))
                                }
                            }) {
                            IconButton(
                                onClick = {
                                    viewModel.dismissNote()
                                },
                                modifier = Modifier.combinedClickable(
                                    onClick = {},
                                    onLongClick = {
                                        scope.launch {
                                            tooltipState.show()
                                        }
                                    }
                                )
                            ) {
                                Icon(Icons.Rounded.Delete, null)
                            }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(text.text.isNotBlank()) {
            var showMenu by remember { mutableStateOf(false) }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, end = 4.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (viewModel.linkedFileSavingState.value == LinkedFileSavingState.Error) {
                    TextButton(
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        onClick = {
                            readWriteErrorSheetText =
                                context.getString(R.string.note_widget_file_write_error_description)
                        }) {
                        Icon(
                            modifier = Modifier
                                .padding(end = ButtonDefaults.IconSpacing)
                                .size(ButtonDefaults.IconSize),
                            imageVector = Icons.Rounded.ErrorOutline,
                            contentDescription = null,
                        )
                        Text(stringResource(R.string.note_widget_file_write_error))
                    }
                } else if (viewModel.linkedFileReadError.value) {
                    TextButton(
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        onClick = {
                            readWriteErrorSheetText =
                                context.getString(R.string.note_widget_file_read_error_description)
                        }) {
                        Icon(
                            modifier = Modifier
                                .padding(end = ButtonDefaults.IconSpacing)
                                .size(ButtonDefaults.IconSize),
                            imageVector = Icons.Rounded.ErrorOutline,
                            contentDescription = null,
                        )
                        Text(stringResource(R.string.note_widget_file_read_error))
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Rounded.MoreVert, stringResource(R.string.action_more_actions))
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.notes_widget_action_new)) },
                            leadingIcon = {
                                Icon(Icons.Rounded.Add, null)
                            },
                            onClick = {
                                val newWidget = NotesWidget(
                                    id = UUID.randomUUID(),
                                )
                                onWidgetAdd(newWidget, 1)
                                showMenu = false
                            },
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.menu_share)) },
                            leadingIcon = {
                                Icon(Icons.Rounded.Share, null)
                            },
                            onClick = {
                                Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, text.text)
                                    context.startActivity(Intent.createChooser(this, null))
                                }
                                showMenu = false
                            },
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.notes_widget_action_save)) },
                            leadingIcon = {
                                Icon(Icons.Rounded.SaveAlt, null)
                            },
                            onClick = {
                                val fileName = getDefaultNoteFileName(context)
                                exportLauncher.launch(fileName)
                                showMenu = false
                            },
                        )
                        if (widget.config.linkedFile == null) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.note_widget_link_file)) },
                                leadingIcon = {
                                    Icon(Icons.Rounded.Link, null)
                                },
                                onClick = {
                                    linkFileLauncher.launch(getDefaultNoteFileName(context))
                                    showMenu = false
                                },
                            )
                        } else {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.note_widget_action_unlink_file)) },
                                leadingIcon = {
                                    Icon(Icons.Rounded.LinkOff, null)
                                },
                                onClick = {
                                    viewModel.unlinkFile(context)
                                    showMenu = false
                                },
                            )
                        }
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.notes_widget_action_dismiss)) },
                            leadingIcon = {
                                Icon(Icons.Rounded.Delete, null)
                            },
                            onClick = {
                                val wasLast = isLastWidget != false

                                if (wasLast) {
                                    viewModel.updateWidgetContent(NotesWidgetConfig())
                                } else {
                                    viewModel.dismissWidget(widget)
                                }

                                lifecycleOwner.lifecycleScope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = context.getString(R.string.notes_widget_dismissed),
                                        actionLabel = context.getString(R.string.action_undo),
                                        duration = SnackbarDuration.Short,
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        if (wasLast) {
                                            viewModel.updateWidgetContent(widget.config)
                                        } else {
                                            onWidgetAdd(widget, 0)
                                        }
                                    }
                                }

                                showMenu = false
                            },
                        )
                    }
                }
            }
        }
    }

    if (readWriteErrorSheetText != null) {
        NoteReadWriteErrorSheet(
            message = readWriteErrorSheetText!!,
            onDismiss = { readWriteErrorSheetText = null },
            onRelink = {
                linkFileLauncher.launch(
                    context.getString(
                        R.string.notes_widget_export_filename,
                        ZonedDateTime.now().format(
                            DateTimeFormatter.ISO_INSTANT
                        )
                    )
                )
            },
            onUnlink = {
                viewModel.unlinkFile(context)
            }
        )
    }
}

@Composable
fun NoteWidgetConflictResolveSheet(
    localContent: String,
    fileContent: String,
    onResolve: (LinkedFileConflictStrategy) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var selectedStrategy by remember { mutableStateOf<LinkedFileConflictStrategy?>(null) }
    BottomSheetDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(
                onClick = { onResolve(selectedStrategy ?: return@Button) },
                enabled = selectedStrategy != null,
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
            ) {
                Icon(
                    Icons.Rounded.CheckCircleOutline,
                    null,
                    modifier = Modifier
                        .padding(end = ButtonDefaults.IconSpacing)
                        .size(ButtonDefaults.IconSize)
                )
                Text(stringResource(R.string.note_widget_conflict_keep_selected))
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onResolve(LinkedFileConflictStrategy.Unlink) },
                contentPadding = ButtonDefaults.TextButtonWithIconContentPadding,
            ) {
                Icon(
                    Icons.Rounded.LinkOff,
                    null,
                    modifier = Modifier
                        .padding(end = ButtonDefaults.IconSpacing)
                        .size(ButtonDefaults.IconSize)
                )
                Text(stringResource(R.string.note_widget_action_unlink_file))
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(it),
        ) {

            Text(
                stringResource(R.string.note_widget_conflict_description),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                stringResource(R.string.note_widget_conflict_local_version),
                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
                style = MaterialTheme.typography.titleSmall
            )
            SelectableNoteContent(
                content = localContent,
                selected = selectedStrategy == LinkedFileConflictStrategy.KeepLocal,
                onSelect = { selectedStrategy = LinkedFileConflictStrategy.KeepLocal },
            )
            Text(
                stringResource(R.string.note_widget_conflict_file_version),
                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
                style = MaterialTheme.typography.titleSmall
            )
            SelectableNoteContent(
                content = fileContent,
                selected = selectedStrategy == LinkedFileConflictStrategy.KeepFile,
                onSelect = { selectedStrategy = LinkedFileConflictStrategy.KeepFile },
            )
        }
    }
}

@Composable
fun SelectableNoteContent(
    content: String,
    selected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val color =
        if (selected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        border = BorderStroke(
            if (selected) 2.dp else 1.dp,
            if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.outlineVariant
        ),
        color = color,
        shape = MaterialTheme.shapes.small,
    ) {
        Box(
            modifier = Modifier
                .combinedClickable(
                    onClick = onSelect,
                    onLongClick = { expanded = !expanded },
                )
                .animateContentSize() then if (expanded) Modifier.heightIn(min = 100.dp) else Modifier.height(
                100.dp
            ),
        ) {
            MarkdownText(
                modifier = Modifier
                    .wrapContentHeight(align = Alignment.Top, unbounded = true)
                    .padding(16.dp),
                text = content, onTextChange = {}
            )
            if (!expanded) {
                Canvas(
                    modifier = Modifier
                        .height(32.dp)
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                ) {
                    drawRect(
                        brush = Brush.verticalGradient(
                            0f to color.copy(alpha = 0f),
                            1f to color.copy(alpha = 0.8f),
                        ),
                    )
                }
            }
            IconButton(
                modifier = Modifier.align(Alignment.TopEnd),
                onClick = onSelect
            ) {
                Icon(
                    if (selected)
                        Icons.Rounded.CheckCircle
                    else
                        Icons.Rounded.RadioButtonUnchecked,
                    tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = null
                )
            }
            IconButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                onClick = { expanded = !expanded }
            ) {
                Icon(
                    if (expanded)
                        Icons.Rounded.ExpandLess
                    else
                        Icons.Rounded.ExpandMore,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun NoteReadWriteErrorSheet(
    message: String,
    onDismiss: () -> Unit,
    onRelink: () -> Unit,
    onUnlink: () -> Unit,
) {
    BottomSheetDialog(
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(top = 8.dp, bottom = 16.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .padding(horizontal = 24.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onRelink()
                        onDismiss()
                    }
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Rounded.Link, null)
                Text(
                    stringResource(R.string.note_widget_action_relink_file),
                    modifier = Modifier.padding(start = 16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onUnlink()
                        onDismiss()
                    }
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Rounded.LinkOff, null)
                Text(
                    stringResource(R.string.note_widget_action_unlink_file),
                    modifier = Modifier.padding(start = 16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

fun getDefaultNoteFileName(context: Context): String {
    return context.getString(
        R.string.notes_widget_export_filename,
        ZonedDateTime.now().format(
            DateTimeFormatter.ISO_INSTANT
        )
    ) + ".md"
}