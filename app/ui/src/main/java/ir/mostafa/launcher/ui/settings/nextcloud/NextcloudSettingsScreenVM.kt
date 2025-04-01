package ir.mostafa.launcher.ui.settings.nextcloud

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.mostafa.launcher.accounts.Account
import ir.mostafa.launcher.accounts.AccountType
import ir.mostafa.launcher.accounts.AccountsRepository
import ir.mostafa.launcher.preferences.search.FileSearchSettings
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NextcloudSettingsScreenVM: ViewModel(), KoinComponent {
    private val accountsRepository: AccountsRepository by inject()
    private val fileSearchSettings: FileSearchSettings by inject()

    val nextcloudUser = mutableStateOf<Account?>(null)
    val loading = mutableStateOf(true)

    fun onResume() {
        viewModelScope.launch {
            loading.value = true
            nextcloudUser.value = accountsRepository.getCurrentlySignedInAccount(AccountType.Nextcloud)
            loading.value = false
        }
    }

    fun signIn(activity: AppCompatActivity) {
        accountsRepository.signin(activity, AccountType.Nextcloud)
    }

    fun signOut() {
        accountsRepository.signout(AccountType.Nextcloud)
        nextcloudUser.value = null
    }

    val searchFiles = fileSearchSettings.nextcloudFiles

    fun setSearchFiles(value: Boolean) {
        fileSearchSettings.setNextcloudFiles(value)
    }
}