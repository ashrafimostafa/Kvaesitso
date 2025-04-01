package ir.mostafa.launcher.ui.settings.integrations

import androidx.lifecycle.ViewModel
import ir.mostafa.launcher.accounts.AccountType
import ir.mostafa.launcher.accounts.AccountsRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class IntegrationsSettingsScreenVM : ViewModel(), KoinComponent {
    private val accountsRepository: AccountsRepository by inject()

    val isGoogleAvailable = accountsRepository.isSupported(AccountType.Google)
}