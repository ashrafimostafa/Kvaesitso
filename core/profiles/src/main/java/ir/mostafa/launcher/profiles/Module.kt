package ir.mostafa.launcher.profiles

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val profilesModule = module {
    single<ProfileManager> { ProfileManager(androidContext(), get()) }
}