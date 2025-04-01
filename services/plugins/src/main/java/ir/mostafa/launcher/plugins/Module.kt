package ir.mostafa.launcher.plugins

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val servicesPluginsModule = module {
    single<PluginService> { PluginServiceImpl(androidContext(), get()) }
}