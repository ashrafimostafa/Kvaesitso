package ir.mostafa.launcher.ui.settings.filesearch

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.mostafa.launcher.accounts.Account
import ir.mostafa.launcher.accounts.AccountType
import ir.mostafa.launcher.accounts.AccountsRepository
import ir.mostafa.launcher.permissions.PermissionGroup
import ir.mostafa.launcher.permissions.PermissionsManager
import ir.mostafa.launcher.plugin.PluginType
import ir.mostafa.launcher.plugins.PluginService
import ir.mostafa.launcher.preferences.search.FileSearchSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FileSearchSettingsScreenVM : ViewModel(), KoinComponent {
    private val fileSearchSettings: FileSearchSettings by inject()
    private val accountsRepository: AccountsRepository by inject()
    private val permissionsManager: PermissionsManager by inject()
    private val pluginService: PluginService by inject()

    val hasFilePermission = permissionsManager.hasPermission(PermissionGroup.ExternalStorage)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val loading = mutableStateOf(true)
    val nextcloudAccount = mutableStateOf<Account?>(null)
    val owncloudAccount = mutableStateOf<Account?>(null)
    val googleAccount = mutableStateOf<Account?>(null)

    val googleAvailable = accountsRepository.isSupported(AccountType.Google)

    val availablePlugins = pluginService.getPluginsWithState(
        type = PluginType.FileSearch,
        enabled = true,
    )

    val enabledPlugins = fileSearchSettings.enabledPlugins


    fun onResume() {
        viewModelScope.launch {
            nextcloudAccount.value =
                accountsRepository.getCurrentlySignedInAccount(AccountType.Nextcloud)
            owncloudAccount.value =
                accountsRepository.getCurrentlySignedInAccount(AccountType.Owncloud)
            googleAccount.value = accountsRepository.getCurrentlySignedInAccount(AccountType.Google)
            loading.value = false
        }
    }

    val localFiles = fileSearchSettings.localFiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
    fun setLocalFiles(localFiles: Boolean) {
        fileSearchSettings.setLocalFiles(localFiles)
    }

    val nextcloud = fileSearchSettings.nextcloudFiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
    fun setNextcloud(nextcloud: Boolean) {
        fileSearchSettings.setNextcloudFiles(nextcloud)
    }

    val gdrive = fileSearchSettings.gdriveFiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
    fun setGdrive(gdrive: Boolean) {
        fileSearchSettings.setGdriveFiles(gdrive)
    }

    val owncloud = fileSearchSettings.owncloudFiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
    fun setOwncloud(owncloud: Boolean) {
        fileSearchSettings.setOwncloudFiles(owncloud)
    }

    fun requestFilePermission(context: AppCompatActivity) {
        permissionsManager.requestPermission(context, PermissionGroup.ExternalStorage)
    }

    fun login(context: AppCompatActivity, accountType: AccountType) {
        accountsRepository.signin(context, accountType)
    }

    fun setPluginEnabled(authority: String, enabled: Boolean) {
        fileSearchSettings.setPluginEnabled(authority, enabled)
    }
}