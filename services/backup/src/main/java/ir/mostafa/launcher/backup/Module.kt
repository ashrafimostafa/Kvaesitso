package ir.mostafa.launcher.backup

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val backupModule = module {
    single { BackupManager(androidContext(), getAll()) }
}