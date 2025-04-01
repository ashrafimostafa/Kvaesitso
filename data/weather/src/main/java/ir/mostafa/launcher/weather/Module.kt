package ir.mostafa.launcher.weather

import ir.mostafa.launcher.weather.brightsky.BrightSkyProvider
import ir.mostafa.launcher.weather.here.HereProvider
import ir.mostafa.launcher.weather.metno.MetNoProvider
import ir.mostafa.launcher.weather.openweathermap.OpenWeatherMapProvider
import ir.mostafa.launcher.weather.plugin.PluginWeatherProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val weatherModule = module {
    single<WeatherRepository> { WeatherRepositoryImpl(androidContext(), get(), get(), get()) }
    factory<WeatherProvider> { (providerId: String) ->
        when (providerId) {
            OpenWeatherMapProvider.Id -> OpenWeatherMapProvider(androidContext())
            MetNoProvider.Id -> MetNoProvider(androidContext(), get())
            HereProvider.Id -> HereProvider(androidContext())
            BrightSkyProvider.Id -> BrightSkyProvider(androidContext())
            else -> PluginWeatherProvider(androidContext(), providerId)
        }
    }
}