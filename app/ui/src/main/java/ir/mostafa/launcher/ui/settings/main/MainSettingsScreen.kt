package ir.mostafa.launcher.ui.settings.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Gesture
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ir.mostafa.launcher.ui.R
import ir.mostafa.launcher.ui.component.preferences.Preference
import ir.mostafa.launcher.ui.component.preferences.PreferenceCategory
import ir.mostafa.launcher.ui.component.preferences.PreferenceScreen
import ir.mostafa.launcher.ui.locals.LocalNavController

@Composable
fun MainSettingsScreen() {
    val navController = LocalNavController.current
    PreferenceScreen(
        title = stringResource(R.string.settings),
    ) {
        item {
            PreferenceCategory {
                Preference(
                    icon = Icons.Rounded.Palette,
                    title = stringResource(id = R.string.preference_screen_appearance),
                    summary = stringResource(id = R.string.preference_screen_appearance_summary),
                    onClick = {
                        navController?.navigate("settings/appearance")
                    }
                )
                Preference(
                    icon = Icons.Rounded.Home,
                    title = stringResource(id = R.string.preference_screen_homescreen),
                    summary = stringResource(id = R.string.preference_screen_homescreen_summary),
                    onClick = {
                        navController?.navigate("settings/homescreen")
                    }
                )
                Preference(
                    icon = Icons.Rounded.Apps,
                    title = stringResource(id = R.string.preference_screen_icons),
                    summary = stringResource(id = R.string.preference_screen_icons_summary),
                    onClick = {
                        navController?.navigate("settings/icons")
                    }
                )
//                Preference(
//                    icon = Icons.Rounded.Search,
//                    title = stringResource(id = R.string.preference_screen_search),
//                    summary = stringResource(id = R.string.preference_screen_search_summary),
//                    onClick = {
//                        navController?.navigate("settings/search")
//                    }
//                )
                Preference(
                    icon = Icons.Rounded.Gesture,
                    title = stringResource(id = R.string.preference_screen_gestures),
                    summary = stringResource(id = R.string.preference_screen_gestures_summary),
                    onClick = {
                        navController?.navigate("settings/gestures")
                    }
                )
//                Preference(
//                    icon = Icons.Rounded.Power,
//                    title = stringResource(id = R.string.preference_screen_integrations),
//                    summary = stringResource(id = R.string.preference_screen_integrations_summary),
//                    onClick = {
//                        navController?.navigate("settings/integrations")
//                    }
//                )
//                Preference(
//                    icon = Icons.Rounded.Extension,
//                    title = stringResource(id = R.string.preference_screen_plugins),
//                    summary = stringResource(id = R.string.preference_screen_plugins_summary),
//                    onClick = {
//                        navController?.navigate("settings/plugins")
//                    }
//                )
//                Preference(
//                    icon = Icons.Rounded.SettingsBackupRestore,
//                    title = stringResource(id = R.string.preference_screen_backup),
//                    summary = stringResource(id = R.string.preference_screen_backup_summary),
//                    onClick = {
//                        navController?.navigate("settings/backup")
//                    }
//                )
//                Preference(
//                    icon = Icons.Rounded.BugReport,
//                    title = stringResource(id = R.string.preference_screen_debug),
//                    summary = stringResource(id = R.string.preference_screen_debug_summary),
//                    onClick = {
//                        navController?.navigate("settings/debug")
//                    }
//                )
                Preference(
                    icon = Icons.Rounded.Info,
                    title = stringResource(id = R.string.preference_screen_about),
                    summary = stringResource(id = R.string.preference_screen_about_summary),
                    onClick = {
                        navController?.navigate("settings/about")
                    }
                )
            }
        }
    }
}