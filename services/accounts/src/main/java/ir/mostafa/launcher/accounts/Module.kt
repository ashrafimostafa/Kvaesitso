package ir.mostafa.launcher.accounts

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val accountsModule = module {
    factory<AccountsRepository> { AccountsRepositoryImpl(androidContext()) }
}