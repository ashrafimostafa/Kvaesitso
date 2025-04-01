package ir.mostafa.launcher.music

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val musicModule = module {
    single<MusicService> { MusicServiceImpl(androidContext(), get(), get()) }
}