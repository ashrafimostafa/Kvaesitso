package ir.mostafa.launcher.ui.settings.osm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.mostafa.launcher.preferences.search.LocationSearchSettings
import ir.mostafa.launcher.ui.R
import ir.mostafa.launcher.ui.component.preferences.PreferenceCategory
import ir.mostafa.launcher.ui.component.preferences.PreferenceScreen
import ir.mostafa.launcher.ui.component.preferences.SwitchPreference
import ir.mostafa.launcher.ui.component.preferences.TextPreference

@Composable
fun OsmSettingsScreen() {
    val viewModel: OsmSettingsScreenVM = viewModel()
    val osmLocations by viewModel.osmLocations.collectAsState()
    val hideUncategorized by viewModel.hideUncategorized.collectAsState()
    val customOverpassUrl by viewModel.customOverpassUrl.collectAsState()

    PreferenceScreen(title = stringResource(R.string.preference_search_osm_locations)) {
        item {
            PreferenceCategory {
                SwitchPreference(
                    title = stringResource(R.string.preference_search_osm_locations),
                    summary = stringResource(R.string.preference_search_osm_locations_summary),
                    value = osmLocations == true,
                    onValueChanged = {
                        viewModel.setOsmLocations(it)
                    },
                )
                SwitchPreference(
                    title = stringResource(R.string.preference_search_locations_hide_uncategorized),
                    summary = stringResource(R.string.preference_search_locations_hide_uncategorized_summary),
                    value = hideUncategorized == true,
                    enabled = osmLocations == true,
                    onValueChanged = {
                        viewModel.setHideUncategorized(it)
                    }
                )
            }
        }

        item {
            PreferenceCategory(stringResource(R.string.preference_category_advanced)) {
                TextPreference(
                    title = stringResource(R.string.preference_search_location_custom_overpass_url),
                    value = customOverpassUrl ?: "",
                    placeholder = LocationSearchSettings.DefaultOverpassUrl,
                    summary = customOverpassUrl?.takeIf { it.isNotBlank() }
                        ?: LocationSearchSettings.DefaultOverpassUrl,
                    onValueChanged = {
                        viewModel.setCustomOverpassUrl(it)
                    },
                    enabled = osmLocations == true,
                )
            }
        }
    }
}