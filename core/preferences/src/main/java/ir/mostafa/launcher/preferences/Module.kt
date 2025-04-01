package ir.mostafa.launcher.preferences

import ir.mostafa.launcher.backup.Backupable
import ir.mostafa.launcher.preferences.search.ContactSearchSettings
import ir.mostafa.launcher.preferences.media.MediaSettings
import ir.mostafa.launcher.preferences.search.CalculatorSearchSettings
import ir.mostafa.launcher.preferences.search.CalendarSearchSettings
import ir.mostafa.launcher.preferences.search.FavoritesSettings
import ir.mostafa.launcher.preferences.search.FileSearchSettings
import ir.mostafa.launcher.preferences.search.LocationSearchSettings
import ir.mostafa.launcher.preferences.search.RankingSettings
import ir.mostafa.launcher.preferences.search.SearchFilterSettings
import ir.mostafa.launcher.preferences.search.ShortcutSearchSettings
import ir.mostafa.launcher.preferences.search.UnitConverterSettings
import ir.mostafa.launcher.preferences.search.WebsiteSearchSettings
import ir.mostafa.launcher.preferences.search.WikipediaSearchSettings
import ir.mostafa.launcher.preferences.ui.BadgeSettings
import ir.mostafa.launcher.preferences.ui.ClockWidgetSettings
import ir.mostafa.launcher.preferences.ui.GestureSettings
import ir.mostafa.launcher.preferences.ui.IconSettings
import ir.mostafa.launcher.preferences.ui.SearchUiSettings
import ir.mostafa.launcher.preferences.ui.UiSettings
import ir.mostafa.launcher.preferences.ui.UiState
import ir.mostafa.launcher.preferences.weather.WeatherSettings
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val preferencesModule = module {
    single { LauncherDataStore(androidContext()) }
    factory<Backupable>(named<LauncherDataStore>()) { get<LauncherDataStore>() }
    factory { MediaSettings(get()) }
    factory { ContactSearchSettings(get()) }
    factory { FileSearchSettings(get()) }
    factory { UnitConverterSettings(get()) }
    factory { BadgeSettings(get()) }
    factory { UiSettings(get()) }
    factory { ShortcutSearchSettings(get()) }
    factory { FavoritesSettings(get()) }
    factory { WikipediaSearchSettings(get()) }
    factory { IconSettings(get()) }
    factory { RankingSettings(get()) }
    factory { CalendarSearchSettings(get()) }
    factory { WebsiteSearchSettings(get()) }
    factory { UiState(get()) }
    factory { SearchUiSettings(get()) }
    factory { WeatherSettings(get()) }
    factory { GestureSettings(get()) }
    factory { CalculatorSearchSettings(get()) }
    factory { ClockWidgetSettings(get()) }
    factory { LocationSearchSettings(get()) }
    factory { SearchFilterSettings(get()) }
}