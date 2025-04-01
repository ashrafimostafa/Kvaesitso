package ir.mostafa.launcher.ui.settings.about

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.mostafa.launcher.ui.component.preferences.Preference
import ir.mostafa.launcher.ui.component.preferences.PreferenceCategory
import ir.mostafa.launcher.ui.component.preferences.PreferenceScreen
import ir.mostafa.launcher.ui.locals.LocalNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AboutSettingsScreen() {
    val viewModel: AboutSettingsScreenVM = viewModel()
    val navController = LocalNavController.current
    val context = LocalContext.current
    PreferenceScreen(title = stringResource(R.string.preference_screen_about)) {
        item {
            PreferenceCategory {
                var appVersion by remember { mutableStateOf<String?>(null) }
                LaunchedEffect(null) {
                    appVersion = withContext(Dispatchers.IO) {
                        context.packageManager.getPackageInfo(
                            context.packageName,
                            0
                        ).versionName
                    }
                }
                var easterEggCounter by remember { mutableStateOf(0) }
                Preference(
                    title = stringResource(R.string.preference_version),
                    summary = "1.0.0",
                    onClick = {
                        //todo
                    }
                )
                Preference(
                    title = "About developer",
                    summary = "This app developed by Mostafa Ashrafi from fall 2023 to winter of 2024",
                    onClick = {
                        //todo
                    }
                )
            }
        }
    }
}