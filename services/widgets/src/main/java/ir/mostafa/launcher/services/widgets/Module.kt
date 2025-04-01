package ir.mostafa.launcher.services.widgets

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val widgetsServiceModule = module {
    single { WidgetsService(androidContext(), get()) }
}