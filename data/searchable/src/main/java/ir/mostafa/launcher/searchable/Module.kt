package ir.mostafa.launcher.searchable

import ir.mostafa.launcher.backup.Backupable
import ir.mostafa.launcher.search.SearchableDeserializer
import ir.mostafa.launcher.search.data.Tag
import org.koin.core.qualifier.named
import org.koin.dsl.module

val searchableModule = module {
    factory <Backupable>(named<SavableSearchableRepository>()) { SavableSearchableRepositoryImpl(
        get(),
        get()
    ) }
    factory <SavableSearchableRepository> { SavableSearchableRepositoryImpl(get(), get()) }
    factory<SearchableDeserializer>(named(Tag.Domain)) { TagDeserializer() }
}