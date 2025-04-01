package ir.mostafa.launcher.devicepose

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val devicePoseModule = module {
    single<DevicePoseProvider> { DevicePoseProvider(androidContext()) }
}