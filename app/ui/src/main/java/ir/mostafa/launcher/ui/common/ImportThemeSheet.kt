package ir.mostafa.launcher.ui.common

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.mostafa.launcher.themes.Theme
import ir.mostafa.launcher.ui.R
import ir.mostafa.launcher.ui.component.BottomSheetDialog
import ir.mostafa.launcher.ui.component.LargeMessage
import ir.mostafa.launcher.ui.component.preferences.SwitchPreference
import ir.mostafa.launcher.ui.locals.LocalDarkTheme
import ir.mostafa.launcher.ui.theme.colorscheme.darkColorSchemeOf
import ir.mostafa.launcher.ui.theme.colorscheme.lightColorSchemeOf
import hct.Hct

@Composable
fun ImportThemeSheet(
    uri: Uri,
    onDismiss: () -> Unit,
) {
    val viewModel: ImportThemeSheetVM = viewModel()

    val context = LocalContext.current

    LaunchedEffect(uri) {
        viewModel.readTheme(context, uri)
    }

    val theme by viewModel.theme
    val error by viewModel.error
    var apply by viewModel.apply

    BottomSheetDialog(
        onDismissRequest = onDismiss,
        confirmButton = if (theme != null && !error) {
            {
                Button(
                    onClick = {
                        viewModel.import()
                        onDismiss()
                    }
                ) {
                    Text(stringResource(R.string.action_import))
                }
            }
        } else null,
    ) {
        if (theme == null && !error) {
            Box(
                modifier = Modifier
                    .padding(it)
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (error) {
            Box(
                modifier = Modifier
                    .padding(it)
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                LargeMessage(
                    icon = Icons.Rounded.ErrorOutline,
                    text = stringResource(R.string.import_theme_error),
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(it)
            ) {
                ThemePreview(
                    theme!!,
                )
                Surface(
                    modifier = Modifier.padding(top = 8.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = MaterialTheme.shapes.medium,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {

                    SwitchPreference(
                        title = stringResource(R.string.import_theme_apply),
                        iconPadding = false,
                        value = apply,
                        onValueChanged = {
                            apply = it
                        })
                }
            }
        }
    }
}

@Composable
fun ThemePreview(
    theme: Theme,
    modifier: Modifier = Modifier,
) {
    val darkMode = LocalDarkTheme.current
    var darkTheme by remember { mutableStateOf(darkMode) }

    val colorScheme = if (darkTheme) darkColorSchemeOf(theme) else lightColorSchemeOf(theme)

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = theme.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            SingleChoiceSegmentedButtonRow {
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                    selected = !darkTheme,
                    onClick = { darkTheme = false }
                ) {
                    Icon(Icons.Rounded.LightMode, null)
                }
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                    selected = darkTheme,
                    onClick = { darkTheme = true }
                ) {
                    Icon(Icons.Rounded.DarkMode, null)
                }
            }
        }
    MaterialTheme(
        colorScheme = colorScheme
    ) {
        Column(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(start = 14.dp, end = 14.dp, bottom = 14.dp, top = 14.dp)
        ) {
            Row {
                ColorSwatch(color = MaterialTheme.colorScheme.primary, darkTheme = darkTheme)
                ColorSwatch(color = MaterialTheme.colorScheme.onPrimary, darkTheme = darkTheme)
                ColorSwatch(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    darkTheme = darkTheme
                )
                ColorSwatch(
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    darkTheme = darkTheme
                )
            }
            Row {
                ColorSwatch(color = MaterialTheme.colorScheme.secondary, darkTheme = darkTheme)
                ColorSwatch(color = MaterialTheme.colorScheme.onSecondary, darkTheme = darkTheme)
                ColorSwatch(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    darkTheme = darkTheme
                )
                ColorSwatch(
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    darkTheme = darkTheme
                )
            }
            Row {
                ColorSwatch(color = MaterialTheme.colorScheme.tertiary, darkTheme = darkTheme)
                ColorSwatch(color = MaterialTheme.colorScheme.onTertiary, darkTheme = darkTheme)
                ColorSwatch(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    darkTheme = darkTheme
                )
                ColorSwatch(
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    darkTheme = darkTheme
                )
            }
            Row {
                ColorSwatch(color = MaterialTheme.colorScheme.error, darkTheme = darkTheme)
                ColorSwatch(color = MaterialTheme.colorScheme.onError, darkTheme = darkTheme)
                ColorSwatch(color = MaterialTheme.colorScheme.errorContainer, darkTheme = darkTheme)
                ColorSwatch(
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    darkTheme = darkTheme
                )
            }
            Row {
                ColorSwatch(color = MaterialTheme.colorScheme.surfaceDim, darkTheme = darkTheme)
                ColorSwatch(color = MaterialTheme.colorScheme.surface, darkTheme = darkTheme)
                ColorSwatch(color = MaterialTheme.colorScheme.surfaceBright, darkTheme = darkTheme)
            }
            Row {
                ColorSwatch(
                    color = MaterialTheme.colorScheme.surfaceContainerLowest,
                    darkTheme = darkTheme
                )
                ColorSwatch(
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    darkTheme = darkTheme
                )
                ColorSwatch(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    darkTheme = darkTheme
                )
                ColorSwatch(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    darkTheme = darkTheme
                )
                ColorSwatch(
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    darkTheme = darkTheme
                )
            }
            Row {
                ColorSwatch(color = MaterialTheme.colorScheme.onSurface, darkTheme = darkTheme)
                ColorSwatch(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    darkTheme = darkTheme
                )
                ColorSwatch(color = MaterialTheme.colorScheme.outline, darkTheme = darkTheme)
                ColorSwatch(color = MaterialTheme.colorScheme.outlineVariant, darkTheme = darkTheme)
            }
            Row {
                ColorSwatch(
                    color = MaterialTheme.colorScheme.inverseSurface,
                    darkTheme = darkTheme,
                    weight = 2f
                )
                ColorSwatch(
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    darkTheme = darkTheme
                )
                ColorSwatch(color = MaterialTheme.colorScheme.inversePrimary, darkTheme = darkTheme)
            }

        }
    }
    }
}

@Composable
fun RowScope.ColorSwatch(
    color: Color,
    darkTheme: Boolean,
    weight: Float = 1f,
) {
    val borderColor = Color(Hct.fromInt(color.toArgb()).let {
        val tone = if (darkTheme) 30f else 80f
        it.apply {
            this.tone = tone.toDouble()
        }.toInt()
    })
    Box(
        modifier = Modifier
            .weight(weight)
            .padding(2.dp)
            .height(36.dp)
            .clip(MaterialTheme.shapes.small)
            .border(
                1.dp,
                borderColor,
                MaterialTheme.shapes.small
            )
            .background(color),
    )
}