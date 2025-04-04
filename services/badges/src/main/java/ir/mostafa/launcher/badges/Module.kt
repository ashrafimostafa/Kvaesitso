package ir.mostafa.launcher.badges

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val badgesModule = module {
    single<BadgeService> { BadgeServiceImpl(androidContext(), get()) }
}