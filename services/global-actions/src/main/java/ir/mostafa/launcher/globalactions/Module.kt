package ir.mostafa.launcher.globalactions

import org.koin.dsl.module

val globalActionsModule = module {
    single { GlobalActionsService() }
}