package ir.mostafa.launcher.themes

import ir.mostafa.launcher.backup.Backupable
import org.koin.core.qualifier.named
import org.koin.dsl.module

val themesModule = module {
    factory<Backupable>(named<ThemeRepository>()) { ThemeRepository(get(), get()) }
    factory { ThemeRepository(get(), get()) }
}