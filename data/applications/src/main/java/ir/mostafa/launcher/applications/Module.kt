package ir.mostafa.launcher.applications

import ir.mostafa.launcher.search.SearchableDeserializer
import ir.mostafa.launcher.search.Application
import ir.mostafa.launcher.search.SearchableRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val applicationsModule = module {
    factory<SearchableRepository<Application>>(named<Application>()) { get<AppRepository>() }
    single<AppRepository> { AppRepositoryImpl(androidContext(), get()) }
    factory<SearchableDeserializer>(named(LauncherApp.Domain)) { LauncherAppDeserializer(androidContext()) }
}