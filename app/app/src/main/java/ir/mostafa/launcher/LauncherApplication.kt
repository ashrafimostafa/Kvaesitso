package ir.mostafa.launcher

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.SvgDecoder
import ir.mostafa.launcher.accounts.accountsModule
import ir.mostafa.launcher.applications.applicationsModule
import ir.mostafa.launcher.appshortcuts.appShortcutsModule
import ir.mostafa.launcher.backup.backupModule
import ir.mostafa.launcher.badges.badgesModule
import ir.mostafa.launcher.calculator.calculatorModule
import ir.mostafa.launcher.calendar.calendarModule
import ir.mostafa.launcher.contacts.contactsModule
import ir.mostafa.launcher.data.customattrs.customAttrsModule
import ir.mostafa.launcher.searchable.searchableModule
import ir.mostafa.launcher.files.filesModule
import ir.mostafa.launcher.icons.iconsModule
import ir.mostafa.launcher.music.musicModule
import ir.mostafa.launcher.search.searchModule
import ir.mostafa.launcher.unitconverter.unitConverterModule
import ir.mostafa.launcher.websites.websitesModule
import ir.mostafa.launcher.widgets.widgetsModule
import ir.mostafa.launcher.wikipedia.wikipediaModule
import ir.mostafa.launcher.database.databaseModule
import ir.mostafa.launcher.debug.initDebugMode
import ir.mostafa.launcher.globalactions.globalActionsModule
import ir.mostafa.launcher.notifications.notificationsModule
import ir.mostafa.launcher.locations.locationsModule
import ir.mostafa.launcher.permissions.permissionsModule
import ir.mostafa.launcher.data.plugins.dataPluginsModule
import ir.mostafa.launcher.devicepose.devicePoseModule
import ir.mostafa.launcher.plugins.servicesPluginsModule
import ir.mostafa.launcher.preferences.preferencesModule
import ir.mostafa.launcher.profiles.profilesModule
import ir.mostafa.launcher.searchactions.searchActionsModule
import ir.mostafa.launcher.services.favorites.favoritesModule
import ir.mostafa.launcher.services.tags.servicesTagsModule
import ir.mostafa.launcher.services.widgets.widgetsServiceModule
import ir.mostafa.launcher.themes.themesModule
import ir.mostafa.launcher.weather.weatherModule
import kotlinx.coroutines.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import kotlin.coroutines.CoroutineContext

class LauncherApplication : Application(), CoroutineScope, ImageLoaderFactory {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + SupervisorJob()

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.BUILD_TYPE == "debug") initDebugMode()

        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@LauncherApplication)
            modules(
                listOf(
                    accountsModule,
                    applicationsModule,
                    appShortcutsModule,
                    baseModule,
                    calculatorModule,
                    badgesModule,
                    calendarModule,
                    contactsModule,
                    customAttrsModule,
                    databaseModule,
                    favoritesModule,
                    searchableModule,
                    filesModule,
                    globalActionsModule,
                    iconsModule,
                    musicModule,
                    notificationsModule,
                    permissionsModule,
                    preferencesModule,
                    searchModule,
                    searchActionsModule,
                    themesModule,
                    unitConverterModule,
                    weatherModule,
                    websitesModule,
                    widgetsModule,
                    wikipediaModule,
                    locationsModule,
                    servicesTagsModule,
                    widgetsServiceModule,
                    dataPluginsModule,
                    servicesPluginsModule,
                    backupModule,
                    devicePoseModule,
                    profilesModule,
                )
            )
        }
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(applicationContext)
            .components {
                add(SvgDecoder.Factory())
            }
            .crossfade(true)
            .crossfade(200)
            .build()
    }
}