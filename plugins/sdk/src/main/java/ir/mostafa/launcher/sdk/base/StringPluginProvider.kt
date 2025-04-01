package ir.mostafa.launcher.sdk.base

import android.net.Uri
import ir.mostafa.launcher.plugin.config.QueryPluginConfig
import ir.mostafa.launcher.plugin.contracts.SearchPluginContract

abstract class StringPluginProvider<T>(
    config: QueryPluginConfig,
) : QueryPluginProvider<String, T>(config) {

    override fun getQuery(uri: Uri): String? {
        return uri.getQueryParameter(SearchPluginContract.Paths.QueryParam)
    }
}