package ir.mostafa.launcher.appshortcuts

import ir.mostafa.launcher.search.AppShortcut
import ir.mostafa.launcher.search.SearchableDeserializer
import ir.mostafa.launcher.search.SearchableRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appShortcutsModule = module {
    factory<AppShortcutRepository> { AppShortcutRepositoryImpl(androidContext(), get(), get()) }
    factory<SearchableRepository<AppShortcut>>(named<AppShortcut>()) { get<AppShortcutRepository>() }
    factory<SearchableDeserializer>(named(LauncherShortcut.Domain)) { LauncherShortcutDeserializer(androidContext()) }
    factory<SearchableDeserializer>(named(LegacyShortcut.Domain)) { LegacyShortcutDeserializer(androidContext()) }
}