package ir.mostafa.launcher.ui.settings.log

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import ir.mostafa.launcher.debug.DebugInformationDumper
import ir.mostafa.launcher.ktx.tryStartActivity
import ir.mostafa.launcher.ui.R
import ir.mostafa.launcher.ui.component.preferences.PreferenceScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.util.regex.Pattern

@Composable
fun LogScreen() {
    var lines by remember { mutableStateOf(emptyList<LogcatLine>()) }
    val listState = rememberLazyListState()

    LaunchedEffect(null) {
        val process = Runtime.getRuntime().exec("/system/bin/logcat -v time")

        val pattern = Pattern.compile(
            "^(\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3})\\s+" +  /* timestamp [1] */
                    "(\\w)/(.+?)\\(\\s*(\\d+)\\): (.*)$"
        )  /* level, tag, pid, msg [2-5] */

        launch(Dispatchers.IO) {
            val inputStream = process.inputStream.bufferedReader()
            while (isActive) {
                val line = try {
                    val line = inputStream.readLine() ?: continue
                    val matcher = pattern.matcher(line)
                    if (matcher.matches()) {
                        FormattedLogcatLine(
                            message = matcher.group(5) ?: "",
                            tag = matcher.group(3) ?: "",
                            level = matcher.group(2) ?: "",
                            timestamp = matcher.group(1) ?: "<no date>",
                        )
                    } else {
                        RawLogcatLine(line)
                    }
                } catch (e: IOException) {
                    break
                }
                lines = (lines + line)
            }
        }
        try {
            awaitCancellation()
        } finally {
            process.destroy()
        }
    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    PreferenceScreen(
        title = stringResource(id = R.string.preference_logs),
        topBarActions = {
            IconButton(onClick = {
                scope.launch {
                    val path = DebugInformationDumper().dump(context)
                    context.tryStartActivity(
                        Intent.createChooser(
                            Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_STREAM, FileProvider.getUriForFile(
                                        context,
                                        context.applicationContext.packageName + ".fileprovider",
                                        File(path)
                                    )
                                )
                            }, null
                        )
                    )
                }
            }) {
                Icon(Icons.Rounded.Share, contentDescription = null)
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                listState.canScrollForward,
                enter = scaleIn(),
                exit = scaleOut(),
            ) {
                SmallFloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    onClick = {
                    scope.launch {
                        listState.animateScrollToItem(lines.lastIndex)
                    }
                }) {
                    Icon(Icons.Rounded.ArrowDownward, null)
                }
            }
        },
        lazyColumnState = listState,
    ) {
        items(lines) {
            if (it is RawLogcatLine) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = it.line ?: "",
                    style = MaterialTheme.typography.bodySmall
                )
            } else if (it is FormattedLogcatLine) {
                val contentColor = when (it.level) {
                    "E" -> MaterialTheme.colorScheme.onErrorContainer
                    "W" -> MaterialTheme.colorScheme.onPrimaryContainer
                    "D" -> MaterialTheme.colorScheme.onSurfaceVariant
                    else -> MaterialTheme.colorScheme.onSurface
                }
                val bgColor = when (it.level) {
                    "E" -> MaterialTheme.colorScheme.errorContainer
                    "W" -> MaterialTheme.colorScheme.primaryContainer
                    "D" -> MaterialTheme.colorScheme.surfaceVariant
                    else -> MaterialTheme.colorScheme.surface
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(bgColor)
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            when (it.level) {
                                "E" -> Icons.Rounded.Error
                                "W" -> Icons.Rounded.Warning
                                "D" -> Icons.Rounded.BugReport
                                else -> Icons.Rounded.Info
                            },
                            null,
                            tint = contentColor
                        )
                        Text(
                            modifier = Modifier.padding(start = 8.dp),
                            text = it.timestamp + " • " + it.tag,
                            style = MaterialTheme.typography.labelSmall,
                            color = contentColor
                        )
                    }
                    Text(
                        modifier = Modifier.padding(top = 8.dp),
                        text = it.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = contentColor
                    )
                }
            }
        }
    }
}

sealed interface LogcatLine

data class FormattedLogcatLine(
    val level: String,
    val timestamp: String,
    val tag: String,
    val message: String,
) : LogcatLine

data class RawLogcatLine(val line: String) : LogcatLine