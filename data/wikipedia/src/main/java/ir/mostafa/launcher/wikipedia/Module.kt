package ir.mostafa.launcher.wikipedia

import ir.mostafa.launcher.search.Article
import ir.mostafa.launcher.search.SearchableDeserializer
import ir.mostafa.launcher.search.SearchableRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val wikipediaModule = module {
    single<SearchableRepository<Article>>(named<Article>()) { WikipediaRepository(androidContext(), get()) }
    factory<SearchableDeserializer>(named(Wikipedia.Domain)) { WikipediaDeserializer(androidContext()) }
}