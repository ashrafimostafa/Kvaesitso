package ir.mostafa.launcher.ui.settings.plugins

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import ir.mostafa.launcher.plugin.PluginPackage
import ir.mostafa.launcher.ui.R
import ir.mostafa.launcher.ui.component.LargeMessage
import ir.mostafa.launcher.ui.component.preferences.Preference
import ir.mostafa.launcher.ui.component.preferences.PreferenceCategory
import ir.mostafa.launcher.ui.component.preferences.PreferenceScreen
import ir.mostafa.launcher.ui.locals.LocalNavController

@Composable
fun PluginsSettingsScreen() {
    val viewModel: PluginsSettingsScreenVM = viewModel()
    val pluginPackages by viewModel.pluginPackages.collectAsState(null)
    val enabledPackages by viewModel.enabledPluginPackages.collectAsState(emptyList())
    val disabledPackages by viewModel.disabledPluginPackages.collectAsState(emptyList())
    PreferenceScreen(
        title = stringResource(R.string.preference_screen_plugins),
        helpUrl = "mostafaashrafi.ir/plugins"
    ) {
        when {
            pluginPackages?.isEmpty() == true -> {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillParentMaxHeight()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        LargeMessage(
                            icon = Icons.Rounded.Extension,
                            text = stringResource(R.string.no_plugins_installed),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            else -> {
                if (enabledPackages.isNotEmpty()) {
                    item {
                        PreferenceCategory("Enabled") {
                            for (plugin in enabledPackages) {
                                PluginPreference(viewModel, plugin)
                            }
                        }
                    }
                }
                if (disabledPackages.isNotEmpty()) {
                    item {
                        PreferenceCategory("Installed") {
                            for (plugin in disabledPackages) {
                                PluginPreference(viewModel, plugin)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PluginPreference(viewModel: PluginsSettingsScreenVM, plugin: PluginPackage) {
    val navController = LocalNavController.current
    val icon by remember(plugin.packageName) {
        viewModel.getIcon(plugin)
    }.collectAsState(null)
    Preference(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(plugin.label)
            }
        },
        summary = plugin.description?.let { { Text(it) } },
        icon = {
            AsyncImage(
                model = icon,
                contentDescription = null,
                modifier = Modifier.size(36.dp)
            )
        },
        onClick = {
            navController?.navigate("settings/plugins/${plugin.packageName}")
        }
    )
}