package ir.mostafa.launcher.data.customattrs

import ir.mostafa.launcher.backup.Backupable
import org.koin.core.qualifier.named
import org.koin.dsl.module

val customAttrsModule = module {
    factory<Backupable>(named<CustomAttributesRepository>()) { CustomAttributesRepositoryImpl(get(), get()) }
    factory<CustomAttributesRepository> { CustomAttributesRepositoryImpl(get(), get()) }
}