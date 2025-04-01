package ir.mostafa.launcher.ui.settings.media

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.mostafa.launcher.ui.BuildConfig
import ir.mostafa.launcher.ui.R
import ir.mostafa.launcher.ui.component.MissingPermissionBanner
import ir.mostafa.launcher.ui.component.ShapedLauncherIcon
import ir.mostafa.launcher.ui.component.preferences.CheckboxPreference
import ir.mostafa.launcher.ui.component.preferences.Preference
import ir.mostafa.launcher.ui.component.preferences.PreferenceCategory
import ir.mostafa.launcher.ui.component.preferences.PreferenceScreen

@Composable
fun MediaIntegrationSettingsScreen() {
    val context = LocalContext.current
    val viewModel: MediaIntegrationSettingsScreenVM = viewModel()
    val hasPermission by viewModel.hasPermission.collectAsStateWithLifecycle(null)
    val loading by viewModel.loading

    val density = LocalDensity.current

    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(null) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.onResume(density = density.density)
        }
    }

    PreferenceScreen(
        stringResource(R.string.preference_media_integration),
        helpUrl = "mostafaashrafi.ir"
    ) {
        if (loading) {
            item {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        item {
            AnimatedVisibility(hasPermission == false) {
                MissingPermissionBanner(
                    text = stringResource(R.string.missing_permission_music_widget),
                    onClick = {
                        viewModel.requestNotificationPermission(context as AppCompatActivity)
                    },
                    modifier = Modifier.padding(16.dp)
                )
            }
            PreferenceCategory(
                stringResource(R.string.preference_category_media_apps)
            ) {
                val apps by viewModel.appList
                for (app in apps) {
                    val icon by app.icon.collectAsState(null)
                    CheckboxPreference(
                        icon = {
                            ShapedLauncherIcon(size = 32.dp, icon = { icon })
                        },
                        title = app.label,
                        value = app.isChecked,
                        onValueChanged = {
                            viewModel.onAppChecked(app, it)
                        }
                    )
                }
            }
        }
        if (BuildConfig.DEBUG) {
            item {
                PreferenceCategory(stringResource(R.string.preference_category_debug)) {
                    Preference(
                        title = "Reset widget",
                        summary = "Clear all music data",
                        onClick = {
                            viewModel.resetWidget()
                        }
                    )
                }
            }
        }
    }
}