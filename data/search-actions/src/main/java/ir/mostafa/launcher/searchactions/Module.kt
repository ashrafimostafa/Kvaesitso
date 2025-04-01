package ir.mostafa.launcher.searchactions

import ir.mostafa.launcher.backup.Backupable
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val searchActionsModule = module {
    factory<Backupable>(named<SearchActionRepository>()) { SearchActionRepositoryImpl(androidContext(), get()) }
    factory<SearchActionRepository> { SearchActionRepositoryImpl(androidContext(), get()) }
    single<SearchActionService> { SearchActionServiceImpl(androidContext(), get(), TextClassifierImpl()) }
}