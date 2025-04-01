package ir.mostafa.launcher.data.plugins

import ir.mostafa.launcher.database.AppDatabase
import ir.mostafa.launcher.plugin.PluginRepository
import org.koin.dsl.module

val dataPluginsModule = module {
    factory<PluginRepository> { PluginRepositoryImpl(get<AppDatabase>().pluginDao()) }
}