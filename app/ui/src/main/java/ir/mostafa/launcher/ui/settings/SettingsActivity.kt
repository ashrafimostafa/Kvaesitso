package ir.mostafa.launcher.ui.settings

import android.content.Intent
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ir.mostafa.launcher.licenses.AppLicense
import ir.mostafa.launcher.licenses.OpenSourceLicenses
import ir.mostafa.launcher.ui.base.BaseActivity
import ir.mostafa.launcher.ui.base.ProvideCompositionLocals
import ir.mostafa.launcher.ui.locals.LocalDarkTheme
import ir.mostafa.launcher.ui.locals.LocalNavController
import ir.mostafa.launcher.ui.locals.LocalWallpaperColors
import ir.mostafa.launcher.ui.overlays.OverlayHost
import ir.mostafa.launcher.ui.settings.about.AboutSettingsScreen
import ir.mostafa.launcher.ui.settings.appearance.AppearanceSettingsScreen
import ir.mostafa.launcher.ui.settings.backup.BackupSettingsScreen
import ir.mostafa.launcher.ui.settings.buildinfo.BuildInfoSettingsScreen
import ir.mostafa.launcher.ui.settings.calendarsearch.CalendarSearchSettingsScreen
import ir.mostafa.launcher.ui.settings.cards.CardsSettingsScreen
import ir.mostafa.launcher.ui.settings.colorscheme.ThemeSettingsScreen
import ir.mostafa.launcher.ui.settings.colorscheme.ThemesSettingsScreen
import ir.mostafa.launcher.ui.settings.crashreporter.CrashReportScreen
import ir.mostafa.launcher.ui.settings.crashreporter.CrashReporterScreen
import ir.mostafa.launcher.ui.settings.debug.DebugSettingsScreen
import ir.mostafa.launcher.ui.settings.easteregg.EasterEggSettingsScreen
import ir.mostafa.launcher.ui.settings.favorites.FavoritesSettingsScreen
import ir.mostafa.launcher.ui.settings.filesearch.FileSearchSettingsScreen
import ir.mostafa.launcher.ui.settings.filterbar.FilterBarSettingsScreen
import ir.mostafa.launcher.ui.settings.gestures.GestureSettingsScreen
import ir.mostafa.launcher.ui.settings.google.GoogleSettingsScreen
import ir.mostafa.launcher.ui.settings.hiddenitems.HiddenItemsSettingsScreen
import ir.mostafa.launcher.ui.settings.homescreen.HomescreenSettingsScreen
import ir.mostafa.launcher.ui.settings.icons.IconsSettingsScreen
import ir.mostafa.launcher.ui.settings.integrations.IntegrationsSettingsScreen
import ir.mostafa.launcher.ui.settings.license.LicenseScreen
import ir.mostafa.launcher.ui.settings.locations.LocationsSettingsScreen
import ir.mostafa.launcher.ui.settings.log.LogScreen
import ir.mostafa.launcher.ui.settings.main.MainSettingsScreen
import ir.mostafa.launcher.ui.settings.media.MediaIntegrationSettingsScreen
import ir.mostafa.launcher.ui.settings.nextcloud.NextcloudSettingsScreen
import ir.mostafa.launcher.ui.settings.osm.OsmSettingsScreen
import ir.mostafa.launcher.ui.settings.owncloud.OwncloudSettingsScreen
import ir.mostafa.launcher.ui.settings.plugins.PluginSettingsScreen
import ir.mostafa.launcher.ui.settings.plugins.PluginsSettingsScreen
import ir.mostafa.launcher.ui.settings.search.SearchSettingsScreen
import ir.mostafa.launcher.ui.settings.searchactions.SearchActionsSettingsScreen
import ir.mostafa.launcher.ui.settings.tags.TagsSettingsScreen
import ir.mostafa.launcher.ui.settings.unitconverter.UnitConverterHelpSettingsScreen
import ir.mostafa.launcher.ui.settings.unitconverter.UnitConverterSettingsScreen
import ir.mostafa.launcher.ui.settings.weather.WeatherIntegrationSettingsScreen
import ir.mostafa.launcher.ui.settings.wikipedia.WikipediaSettingsScreen
import ir.mostafa.launcher.ui.theme.LauncherTheme
import ir.mostafa.launcher.ui.theme.wallpaperColorsAsState
import java.net.URLDecoder
import java.util.UUID

class SettingsActivity : BaseActivity() {

    private var route by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val newRoute = getStartRoute(intent)
        route = newRoute

        setContent {
            val navController = rememberNavController()

            LaunchedEffect(route) {
                try {
                    navController.navigate(route ?: "settings") {
                        popUpTo("settings") {
                            inclusive = true
                        }
                    }
                } catch (e: IllegalArgumentException) {
                    navController.navigate("settings") {
                        popUpTo("settings") {
                            inclusive = true
                        }
                    }
                }
            }
            val wallpaperColors by wallpaperColorsAsState()
            CompositionLocalProvider(
                LocalNavController provides navController,
                LocalWallpaperColors provides wallpaperColors,
            ) {
                ProvideCompositionLocals {
                    LauncherTheme {
                        val systemBarColor = MaterialTheme.colorScheme.surfaceDim
                        val systemBarColorAlt = MaterialTheme.colorScheme.onSurface
                        val isDarkTheme = LocalDarkTheme.current
                        LaunchedEffect(isDarkTheme, systemBarColor, systemBarColorAlt) {
                            enableEdgeToEdge(
                                if (isDarkTheme) SystemBarStyle.dark(systemBarColor.toArgb())
                                else SystemBarStyle.light(
                                    systemBarColor.toArgb(),
                                    systemBarColorAlt.toArgb()
                                )
                            )
                        }
                        OverlayHost {
                            NavHost(
                                navController = navController,
                                startDestination = "settings",
                                exitTransition = {
                                    slideOutHorizontally { -it / 4 }
                                },
                                enterTransition = {
                                    slideInHorizontally { it / 2 } + scaleIn(initialScale = 0.9f) + fadeIn()
                                },
                                popEnterTransition = {
                                    slideInHorizontally { -it / 4 }
                                },
                                popExitTransition = {
                                    slideOutHorizontally { it / 2 } + scaleOut(targetScale = 0.9f) + fadeOut()
                                },
                            ) {
                                composable("settings") {
                                    MainSettingsScreen()
                                }
                                composable("settings/appearance") {
                                    AppearanceSettingsScreen()
                                }
                                composable("settings/homescreen") {
                                    HomescreenSettingsScreen()
                                }
                                composable("settings/icons") {
                                    IconsSettingsScreen()
                                }
                                composable("settings/appearance/themes") {
                                    ThemesSettingsScreen()
                                }
                                composable(
                                    "settings/appearance/themes/{id}",
                                    arguments = listOf(navArgument("id") {
                                        nullable = false
                                    })
                                ) {
                                    val id = it.arguments?.getString("id")?.let {
                                        UUID.fromString(it)
                                    } ?: return@composable
                                    ThemeSettingsScreen(id)
                                }
                                composable("settings/appearance/cards") {
                                    CardsSettingsScreen()
                                }
                                composable("settings/search") {
                                    SearchSettingsScreen()
                                }
                                composable("settings/gestures") {
                                    GestureSettingsScreen()
                                }
                                composable("settings/search/unitconverter") {
                                    UnitConverterSettingsScreen()
                                }
                                composable("settings/search/unitconverter/help") {
                                    UnitConverterHelpSettingsScreen()
                                }
                                composable("settings/search/wikipedia") {
                                    WikipediaSettingsScreen()
                                }
                                composable("settings/search/locations") {
                                    LocationsSettingsScreen()
                                }
                                composable("settings/search/locations/osm") {
                                    OsmSettingsScreen()
                                }
                                composable("settings/search/files") {
                                    FileSearchSettingsScreen()
                                }
                                composable("settings/search/calendar") {
                                    CalendarSearchSettingsScreen()
                                }
                                composable("settings/search/searchactions") {
                                    SearchActionsSettingsScreen()
                                }
                                composable("settings/search/hiddenitems") {
                                    HiddenItemsSettingsScreen()
                                }
                                composable("settings/search/tags") {
                                    TagsSettingsScreen()
                                }
                                composable("settings/search/filterbar") {
                                    FilterBarSettingsScreen()
                                }
                                composable(ROUTE_WEATHER_INTEGRATION) {
                                    WeatherIntegrationSettingsScreen()
                                }
                                composable(ROUTE_MEDIA_INTEGRATION) {
                                    MediaIntegrationSettingsScreen()
                                }
                                composable("settings/favorites") {
                                    FavoritesSettingsScreen()
                                }
                                composable("settings/integrations") {
                                    IntegrationsSettingsScreen()
                                }
                                composable("settings/integrations/nextcloud") {
                                    NextcloudSettingsScreen()
                                }
                                composable("settings/integrations/owncloud") {
                                    OwncloudSettingsScreen()
                                }
                                composable("settings/integrations/google") {
                                    GoogleSettingsScreen()
                                }
                                composable("settings/plugins") {
                                    PluginsSettingsScreen()
                                }
                                composable("settings/plugins/{id}") {
                                    PluginSettingsScreen(
                                        it.arguments?.getString("id") ?: return@composable
                                    )
                                }
                                composable("settings/about") {
                                    AboutSettingsScreen()
                                }
                                composable("settings/about/buildinfo") {
                                    BuildInfoSettingsScreen()
                                }
                                composable("settings/about/easteregg") {
                                    EasterEggSettingsScreen()
                                }
                                composable("settings/debug") {
                                    DebugSettingsScreen()
                                }
                                composable("settings/backup") {
                                    BackupSettingsScreen()
                                }
                                composable("settings/debug/crashreporter") {
                                    CrashReporterScreen()
                                }
                                composable("settings/debug/logs") {
                                    LogScreen()
                                }
                                composable(
                                    "settings/debug/crashreporter/report?fileName={fileName}",
                                    arguments = listOf(navArgument("fileName") {
                                        nullable = false
                                    })
                                ) {
                                    val fileName = it.arguments?.getString("fileName")
                                        ?.let {
                                            URLDecoder.decode(it, "utf8")
                                        }
                                    CrashReportScreen(fileName!!)
                                }
                                composable(
                                    "settings/license?library={libraryName}",
                                    arguments = listOf(navArgument("libraryName") {
                                        nullable = true
                                    })
                                ) {
                                    val libraryName = it.arguments?.getString("libraryName")
                                    val library = remember(libraryName) {
                                        if (libraryName == null) {
                                            AppLicense.get(this@SettingsActivity)
                                        } else {
                                            OpenSourceLicenses.first { it.name == libraryName }
                                        }
                                    }
                                    LicenseScreen(library)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val newRoute = getStartRoute(intent)
        route = newRoute
    }

    private fun getStartRoute(intent: Intent): String? {
        if (intent.data?.host == "ir.mostafa.launcher") {
            return intent.data?.getQueryParameter("route")
        } else {
            return intent.getStringExtra(EXTRA_ROUTE)
        }
    }

    companion object {
        const val EXTRA_ROUTE = "ir.mostafa.launcher.settings.ROUTE"
        const val ROUTE_WEATHER_INTEGRATION = "settings/integrations/weather"
        const val ROUTE_MEDIA_INTEGRATION = "settings/integrations/media"
    }
}