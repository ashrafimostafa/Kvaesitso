package ir.mostafa.launcher.permissions

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val permissionsModule = module {
    single<PermissionsManager> { PermissionsManagerImpl(androidContext()) }
}