package ir.mostafa.launcher.sdk.config

import android.os.Bundle
import ir.mostafa.launcher.plugin.config.WeatherPluginConfig

internal fun WeatherPluginConfig.toBundle(): Bundle {
    return Bundle().apply {
        putLong("minUpdateInterval", minUpdateInterval)
        putBoolean("managedLocation", managedLocation)
    }
}