package ir.mostafa.launcher

import android.content.pm.PackageManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val baseModule = module {
    factory<PackageManager> { androidContext().packageManager }
}