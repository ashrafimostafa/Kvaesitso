package ir.mostafa.launcher.ui.settings.appearance

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.mostafa.launcher.preferences.ColorScheme
import ir.mostafa.launcher.ui.R
import ir.mostafa.launcher.ui.component.preferences.ListPreference
import ir.mostafa.launcher.ui.component.preferences.Preference
import ir.mostafa.launcher.ui.component.preferences.PreferenceCategory
import ir.mostafa.launcher.ui.component.preferences.PreferenceScreen
import ir.mostafa.launcher.ui.locals.LocalNavController

@Composable
fun AppearanceSettingsScreen() {
    val viewModel: AppearanceSettingsScreenVM = viewModel()
    val context = LocalContext.current
    val navController = LocalNavController.current
    val themeName by viewModel.themeName.collectAsStateWithLifecycle(null)
    val compatModeColors by viewModel.compatModeColors.collectAsState()
    PreferenceScreen(title = stringResource(id = R.string.preference_screen_appearance)) {
        item {
            PreferenceCategory {
                val theme by viewModel.colorScheme.collectAsState()
                ListPreference(
                    title = stringResource(id = R.string.preference_theme),
                    items = listOf(
                        stringResource(id = R.string.preference_theme_system) to ColorScheme.System,
                        stringResource(id = R.string.preference_theme_light) to ColorScheme.Light,
                        stringResource(id = R.string.preference_theme_dark) to ColorScheme.Dark,
                    ),
                    value = theme,
                    onValueChanged = { newValue ->
                        if (newValue == null) return@ListPreference
                        viewModel.setColorScheme(newValue)
                    }
                )
//                Preference(
//                    title = stringResource(id = R.string.preference_screen_colors),
//                    summary = themeName,
//                    onClick = {
//                        navController?.navigate("settings/appearance/themes")
//                    }
//                )
                val font by viewModel.font.collectAsState()
//                ListPreference(
//                    title = stringResource(R.string.preference_font),
//                    items = listOf(
//                        "Outfit" to Font.Outfit,
//                        stringResource(R.string.preference_font_system) to Font.System,
//                    ),
//                    value = font,
//                    onValueChanged = {
//                        if (it != null) viewModel.setFont(it)
//                    },
//                    itemLabel = {
//                        val typography = remember(it.value) {
//                            getTypography(context, it.value)
//                        }
//                        Text(it.first, style = typography.titleMedium)
//                    }
//                )

                Preference(
                    title = stringResource(R.string.preference_cards),
                    summary = stringResource(R.string.preference_cards_summary),
                    onClick = {
                        navController?.navigate("settings/appearance/cards")
                    }
                )
            }
        }

//        if (isAtLeastApiLevel(31)) {
//            item {
//                PreferenceCategory(stringResource(R.string.preference_category_advanced)) {
//                    ListPreference(
//                        title = stringResource(R.string.preference_mdy_color_source),
//                        items = listOf(
//                            stringResource(R.string.preference_mdy_color_source_system) to false,
//                            stringResource(R.string.preference_mdy_color_source_wallpaper) to true,
//                        ),
//                        value = compatModeColors,
//                        onValueChanged = {
//                            viewModel.setCompatModeColors(it)
//                        }
//                    )
//                }
//            }
//        }
    }
}