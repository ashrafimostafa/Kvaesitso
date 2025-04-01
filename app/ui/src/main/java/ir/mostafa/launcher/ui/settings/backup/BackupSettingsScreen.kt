package ir.mostafa.launcher.ui.settings.backup

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.mostafa.launcher.ui.common.RestoreBackupSheet
import ir.mostafa.launcher.ui.component.preferences.Preference
import ir.mostafa.launcher.ui.component.preferences.PreferenceCategory
import ir.mostafa.launcher.ui.component.preferences.PreferenceScreen

@Composable
fun BackupSettingsScreen() {
    val viewModel: BackupSettingsScreenVM = viewModel()

    val restoreUri by viewModel.restoreUri

    val showBackupSheet by viewModel.showBackupSheet

    val restoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = {
            viewModel.setRestoreUri(it)
        }
    )

    PreferenceScreen(stringResource(R.string.preference_screen_backup)) {
        item {
            PreferenceCategory {
                Preference(
                    title = stringResource(id = R.string.preference_backup),
                    summary = stringResource(id = R.string.preference_backup_summary),
                    onClick = {
                        viewModel.setShowBackupSheet(true)
                    })
                Preference(
                    title = stringResource(id = R.string.preference_restore),
                    summary = stringResource(id = R.string.preference_restore_summary),
                    onClick = {
                        restoreLauncher.launch(arrayOf("*/*"))
                    })
            }
        }
    }

    val uri = restoreUri

    if (uri != null) {
        RestoreBackupSheet(uri = uri, onDismissRequest = { viewModel.setRestoreUri(null) })
    }

    if(showBackupSheet) {
        CreateBackupSheet(onDismissRequest = {
            viewModel.setShowBackupSheet(false)
        })
    }
}