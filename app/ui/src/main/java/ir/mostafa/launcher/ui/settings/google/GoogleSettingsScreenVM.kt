package ir.mostafa.launcher.ui.settings.google

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

class GoogleSettingsScreenVM: ViewModel(), KoinComponent {
    private val accountsRepository: AccountsRepository by inject()
    private val fileSearchSettings: FileSearchSettings by inject()

    val googleUser = mutableStateOf<Account?>(null)
    val loading = mutableStateOf(true)

    fun onResume() {
        viewModelScope.launch {
            loading.value = true
            googleUser.value = accountsRepository.getCurrentlySignedInAccount(AccountType.Google)
            loading.value = false
        }
    }

    fun signIn(activity: AppCompatActivity) {
        accountsRepository.signin(activity, AccountType.Google)
    }

    fun signOut() {
        accountsRepository.signout(AccountType.Google)
        googleUser.value = null
    }

    val searchFiles = fileSearchSettings.gdriveFiles

    fun setSearchFiles(value: Boolean) {
        fileSearchSettings.setGdriveFiles(value)
    }
}