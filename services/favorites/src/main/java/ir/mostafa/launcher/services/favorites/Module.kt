package ir.mostafa.launcher.services.favorites

import org.koin.dsl.module

val favoritesModule = module {
    factory { FavoritesService(get()) }
}