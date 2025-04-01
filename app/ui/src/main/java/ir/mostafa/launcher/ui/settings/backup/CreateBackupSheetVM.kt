package ir.mostafa.launcher.ui.settings.backup

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.mostafa.launcher.backup.BackupManager
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CreateBackupSheetVM : ViewModel(), KoinComponent {

    private val backupManager: BackupManager by inject()

    val state = mutableStateOf(CreateBackupState.Ready)

    fun reset() {
        state.value = CreateBackupState.Ready
    }

    fun createBackup(uri: Uri) {
        viewModelScope.launch {
            state.value = CreateBackupState.BackingUp
            backupManager.backup(uri)
            state.value = CreateBackupState.BackedUp
        }
    }
}

enum class CreateBackupState {
    Ready,
    BackingUp,
    BackedUp,
}