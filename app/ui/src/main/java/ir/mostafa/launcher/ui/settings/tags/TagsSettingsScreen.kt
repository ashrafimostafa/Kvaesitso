package ir.mostafa.launcher.ui.settings.tags

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Tag
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.mostafa.launcher.ui.R
import ir.mostafa.launcher.ui.component.preferences.Preference
import ir.mostafa.launcher.ui.component.preferences.PreferenceCategory
import ir.mostafa.launcher.ui.component.preferences.PreferenceScreen
import ir.mostafa.launcher.ui.ktx.splitLeadingEmoji

@Composable
fun TagsSettingsScreen() {
    val viewModel: TagsSettingsScreenVM = viewModel()

    val tags by remember { viewModel.tags }.collectAsState(emptyList())

    PreferenceScreen(
        title = stringResource(R.string.preference_screen_tags),
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.createTag.value = true }) {
                Icon(Icons.Rounded.Add, null)
            }
        },
        helpUrl = "https://mostafaashrafi.ir/help"
    ) {
        item {
            PreferenceCategory {
                for (tag in tags) {
                    var showMenu by remember { mutableStateOf(false) }

                    val (emoji, tagName) = remember(tag) {
                        tag.splitLeadingEmoji()
                    }

                    Preference(
                        icon = {
                            if (emoji != null) {
                                Text(emoji)
                            } else {
                                Icon(Icons.Rounded.Tag, null)
                            }
                        },
                        title = { Text(tagName ?: "") },
                        onClick = {
                            viewModel.editTag.value = tag
                        },
                        controls = {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(Icons.Rounded.MoreVert, null)
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.duplicate)) },
                                    leadingIcon = { Icon(Icons.Rounded.ContentCopy, null) },
                                    onClick = {
                                        viewModel.duplicateTag(tag)
                                        showMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.menu_delete)) },
                                    leadingIcon = { Icon(Icons.Rounded.Delete, null) },
                                    onClick = {
                                        viewModel.deleteTag(tag)
                                        showMenu = false
                                    }
                                )
                            }
                        }
                    )
                }
            }
        }
    }
    if (viewModel.editTag.value != null) {
        EditTagSheet(
            tag = viewModel.editTag.value,
            onDismiss = {
                viewModel.editTag.value = null
                viewModel.createTag.value = false
            }
        )
    } else if (viewModel.createTag.value) {
        EditTagSheet(
            tag = null,
            onDismiss = {
                viewModel.createTag.value = false
                viewModel.editTag.value = null
            }
        )
    }
}