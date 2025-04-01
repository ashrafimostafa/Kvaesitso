package ir.mostafa.launcher.files

import ir.mostafa.launcher.files.providers.GDriveFile
import ir.mostafa.launcher.files.providers.LocalFile
import ir.mostafa.launcher.files.providers.NextcloudFile
import ir.mostafa.launcher.files.providers.OwncloudFile
import ir.mostafa.launcher.files.providers.PluginFile
import ir.mostafa.launcher.search.File
import ir.mostafa.launcher.search.SearchableDeserializer
import ir.mostafa.launcher.search.SearchableRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val filesModule = module {
    factory<SearchableRepository<File>>(named<File>()) {
        FileRepository(
            androidContext(),
            get(),
            get()
        )
    }
    factory<SearchableDeserializer>(named(LocalFile.Domain)) { LocalFileDeserializer(androidContext()) }
    factory<SearchableDeserializer>(named(OwncloudFile.Domain)) { OwncloudFileDeserializer() }
    factory<SearchableDeserializer>(named(NextcloudFile.Domain)) { NextcloudFileDeserializer() }
    factory<SearchableDeserializer>(named(GDriveFile.Domain)) { GDriveFileDeserializer() }
    factory<SearchableDeserializer>(named(PluginFile.Domain)) {
        PluginFileDeserializer(
            androidContext(),
            get()
        )
    }
}