package ir.mostafa.launcher.database
import org.koin.dsl.module

val databaseModule = module {
    single { AppDatabase.getInstance(get()) }
}