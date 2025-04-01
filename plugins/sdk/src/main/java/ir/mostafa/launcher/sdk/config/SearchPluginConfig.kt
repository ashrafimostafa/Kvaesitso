package ir.mostafa.launcher.sdk.config

import android.os.Bundle
import ir.mostafa.launcher.plugin.config.QueryPluginConfig

internal fun QueryPluginConfig.toBundle(): Bundle {
    return Bundle().apply {
        putString("storageStrategy", storageStrategy.name)
    }
}