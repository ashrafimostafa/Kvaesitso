package ir.mostafa.launcher.widgets

import ir.mostafa.launcher.backup.Backupable
import org.koin.core.qualifier.named
import org.koin.dsl.module

val widgetsModule = module {
    factory<Backupable>(named<WidgetRepository>()) { WidgetRepositoryImpl(get()) }
    factory<WidgetRepository> { WidgetRepositoryImpl(get()) }
}