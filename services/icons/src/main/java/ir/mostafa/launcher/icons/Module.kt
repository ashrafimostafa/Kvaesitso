package ir.mostafa.launcher.icons

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val iconsModule = module {
    single { IconPackManager(androidContext(), get()) }
    single { IconService(androidContext(), get(), get(), get()) }
}