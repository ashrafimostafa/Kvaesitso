package ir.mostafa.launcher.plugin.config

import android.os.Bundle

fun WeatherPluginConfig(bundle: Bundle): WeatherPluginConfig {
    return WeatherPluginConfig(
        minUpdateInterval = bundle.getLong(
            "minUpdateInterval",
            60 * 60 * 1000L
        ),
        managedLocation = bundle.getBoolean("managedLocation", false)
    )
}