package ir.mostafa.launcher.ui.settings.colorscheme

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.RadioButtonChecked
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.mostafa.launcher.themes.Theme
import ir.mostafa.launcher.ui.R
import ir.mostafa.launcher.ui.common.ImportThemeSheet
import ir.mostafa.launcher.ui.component.preferences.Preference
import ir.mostafa.launcher.ui.component.preferences.PreferenceCategory
import ir.mostafa.launcher.ui.component.preferences.PreferenceScreen
import ir.mostafa.launcher.ui.locals.LocalDarkTheme
import ir.mostafa.launcher.ui.locals.LocalNavController
import ir.mostafa.launcher.ui.theme.colorscheme.darkColorSchemeOf
import ir.mostafa.launcher.ui.theme.colorscheme.lightColorSchemeOf

@Composable
fun ThemesSettingsScreen() {
    val viewModel: ThemesSettingsScreenVM = viewModel()
    val navController = LocalNavController.current
    val context = LocalContext.current

    val selectedTheme by viewModel.selectedTheme.collectAsStateWithLifecycle(null)
    val themes by viewModel.themes.collectAsStateWithLifecycle(emptyList())

    var deleteTheme by remember { mutableStateOf<Theme?>(null) }

    var importThemeUri by remember { mutableStateOf<Uri?>(null) }

    val importIntentLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
        importThemeUri = it
    }

    PreferenceScreen(
        title = stringResource(R.string.preference_screen_colors),
        topBarActions = {
            IconButton(onClick = { importIntentLauncher.launch(arrayOf("*/*")) }) {
                Icon(Icons.Rounded.Download, null)
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.createNew(context) }) {
                Icon(Icons.Rounded.Add, null)
            }
        }
    ) {
        item {
            PreferenceCategory {
                for (theme in themes) {
                    var showMenu by remember { mutableStateOf(false) }
                    Preference(
                        icon = if (theme.id == selectedTheme) Icons.Rounded.RadioButtonChecked else Icons.Rounded.RadioButtonUnchecked,
                        title = theme.name,
                        controls = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                ColorSchemePreview(theme)
                                IconButton(
                                    modifier = Modifier.padding(start = 12.dp),
                                    onClick = { showMenu = true }) {
                                    Icon(Icons.Rounded.MoreVert, null)
                                }
                                DropdownMenu(
                                    expanded = showMenu,
                                    onDismissRequest = { showMenu = false }
                                ) {
                                    if (!theme.builtIn) {
                                        DropdownMenuItem(
                                            leadingIcon = {
                                                Icon(Icons.Rounded.Edit, null)
                                            },
                                            text = { Text(stringResource(R.string.edit)) },
                                            onClick = {
                                                navController?.navigate("settings/appearance/themes/${theme.id}")
                                                showMenu = false
                                            }
                                        )
                                    }
                                    DropdownMenuItem(
                                        leadingIcon = {
                                            Icon(Icons.Rounded.ContentCopy, null)
                                        },
                                        text = { Text(stringResource(R.string.duplicate)) },
                                        onClick = {
                                            viewModel.duplicate(theme)
                                            showMenu = false
                                        }
                                    )
                                    if (!theme.builtIn) {
                                        DropdownMenuItem(
                                            leadingIcon = {
                                                Icon(Icons.Rounded.Share, null)
                                            },
                                            text = { Text(stringResource(R.string.menu_share)) },
                                            onClick = {
                                                viewModel.exportTheme(context, theme)
                                                showMenu = false
                                            }
                                        )
                                        DropdownMenuItem(
                                            leadingIcon = {
                                                Icon(Icons.Rounded.Delete, null)
                                            },
                                            text = { Text(stringResource(R.string.menu_delete)) },
                                            onClick = {
                                                deleteTheme = theme
                                                showMenu = false
                                            }
                                        )
                                    }
                                }
                            }
                        },
                        onClick = {
                            viewModel.selectTheme(theme)
                        }
                    )
                }
            }
        }
    }
    if (deleteTheme != null) {
        AlertDialog(
            onDismissRequest = { deleteTheme = null },
            text = {
                Text(
                    stringResource(
                        R.string.confirmation_delete_color_scheme,
                        deleteTheme!!.name
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.delete(deleteTheme!!)
                        deleteTheme = null
                    }
                ) {
                    Text(stringResource(android.R.string.ok))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { deleteTheme = null }
                ) {
                    Text(stringResource(android.R.string.cancel))
                }
            }
        )
    }

    if (importThemeUri != null) {
        ImportThemeSheet(uri = importThemeUri!!, onDismiss = { importThemeUri = null })
    }
}

@Composable
fun ColorSchemePreview(theme: Theme) {
    val dark = LocalDarkTheme.current
    val scheme = if (dark) darkColorSchemeOf(theme) else lightColorSchemeOf(theme)
    Box(
        modifier = Modifier
            .height(28.dp)
            .width(72.dp)
            .clip(MaterialTheme.shapes.small)
            .border(1.dp, scheme.outlineVariant, MaterialTheme.shapes.small)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .background(scheme.surface)
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .background(scheme.surfaceVariant)
            )
        }

        Row(
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .padding(6.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .size(16.dp)
                    .background(scheme.primary)
            )
            Box(
                modifier = Modifier
                    .padding(vertical = 6.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .size(16.dp)
                    .background(scheme.secondary)
            )
            Box(
                modifier = Modifier
                    .padding(6.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .size(16.dp)
                    .background(scheme.tertiary)
            )
        }
    }
}