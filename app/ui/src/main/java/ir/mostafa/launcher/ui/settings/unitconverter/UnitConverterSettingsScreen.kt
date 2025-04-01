package ir.mostafa.launcher.ui.settings.unitconverter

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.mostafa.launcher.ui.R
import ir.mostafa.launcher.ui.component.preferences.Preference
import ir.mostafa.launcher.ui.component.preferences.PreferenceCategory
import ir.mostafa.launcher.ui.component.preferences.PreferenceScreen
import ir.mostafa.launcher.ui.component.preferences.SwitchPreference
import ir.mostafa.launcher.ui.locals.LocalNavController

@Composable
fun UnitConverterSettingsScreen() {
    val viewModel: UnitConverterSettingsScreenVM = viewModel()
    val navController = LocalNavController.current

    PreferenceScreen(
        title = stringResource(R.string.preference_search_unitconverter),
        helpUrl = "mostafaashrafi.ir/unit-converter"
    ) {
        item {
            PreferenceCategory {
                val unitConverter by viewModel.unitConverter.collectAsState()
                SwitchPreference(
                    title = stringResource(R.string.preference_search_unitconverter),
                    summary = stringResource(R.string.preference_search_unitconverter_summary),
                    value = unitConverter == true,
                    onValueChanged = {
                        viewModel.setUnitConverter(it)
                    }
                )
                val currencyConverter by viewModel.currencyConverter.collectAsState()
                SwitchPreference(
                    title = stringResource(R.string.preference_search_currencyconverter),
                    summary = stringResource(R.string.preference_search_currencyconverter_summary),
                    enabled = unitConverter != false,
                    value = currencyConverter == true,
                    onValueChanged = {
                        viewModel.setCurrencyConverter(it)
                    }
                )
            }
            PreferenceCategory {
                Preference(
                    title = stringResource(R.string.preference_search_supportedunits),
                    icon = Icons.AutoMirrored.Default.Help,
                    onClick = {
                        navController?.navigate("settings/search/unitconverter/help")
                    }
                )
            }
        }

    }
}
