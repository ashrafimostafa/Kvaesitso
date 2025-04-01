package ir.mostafa.launcher.websites

import ir.mostafa.launcher.search.SearchableDeserializer
import ir.mostafa.launcher.search.SearchableRepository
import ir.mostafa.launcher.search.Website
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val websitesModule = module {
    single<SearchableRepository<Website>>(named<Website>()) { WebsiteRepository(androidContext(), get()) }
    factory<SearchableDeserializer>(named(WebsiteImpl.Domain)) { WebsiteDeserializer() }
}