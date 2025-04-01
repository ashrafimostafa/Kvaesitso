package ir.mostafa.launcher.ui.settings.owncloud

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

class OwncloudSettingsScreenVM: ViewModel(), KoinComponent {
    private val accountsRepository: AccountsRepository by inject()
    private val fileSearchSettings: FileSearchSettings by inject()

    val owncloudUser = mutableStateOf<Account?>(null)
    val loading = mutableStateOf(true)

    fun onResume() {
        viewModelScope.launch {
            loading.value = true
            owncloudUser.value = accountsRepository.getCurrentlySignedInAccount(AccountType.Owncloud)
            loading.value = false
        }
    }

    fun signIn(activity: AppCompatActivity) {
        accountsRepository.signin(activity, AccountType.Owncloud)
    }

    fun signOut() {
        accountsRepository.signout(AccountType.Owncloud)
        owncloudUser.value = null
    }

    val searchFiles = fileSearchSettings.owncloudFiles

    fun setSearchFiles(value: Boolean) {
        fileSearchSettings.setOwncloudFiles(value)
    }
}