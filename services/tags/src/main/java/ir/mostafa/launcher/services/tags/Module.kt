package ir.mostafa.launcher.services.tags

import ir.mostafa.launcher.services.tags.impl.TagsServiceImpl
import org.koin.dsl.module

val servicesTagsModule = module {
    single<TagsService> { TagsServiceImpl(get(), get()) }
}