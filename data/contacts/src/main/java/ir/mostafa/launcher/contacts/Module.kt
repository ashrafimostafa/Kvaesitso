package ir.mostafa.launcher.contacts

import ir.mostafa.launcher.search.Contact
import ir.mostafa.launcher.search.SearchableDeserializer
import ir.mostafa.launcher.search.SearchableRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val contactsModule = module {
    factory { ContactRepository(androidContext(), get(), get()) }
    factory<SearchableRepository<Contact>>(named<Contact>()) { get<ContactRepository>() }
    factory<SearchableDeserializer>(named(AndroidContact.Domain)) { ContactDeserializer(get(), get()) }
}