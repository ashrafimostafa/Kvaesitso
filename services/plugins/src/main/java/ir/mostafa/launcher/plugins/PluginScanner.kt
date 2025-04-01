import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import ir.mostafa.launcher.crashreporter.CrashReporter
import ir.mostafa.launcher.plugin.Plugin
import ir.mostafa.launcher.plugin.PluginType

class PluginScanner(
    private val context: Context,
) {
    suspend fun findPlugins(): List<Plugin> {
        val contentResolvers = context.packageManager.queryIntentContentProviders(
            Intent("ir.mostafa.launcher.action.PLUGIN"),
            PackageManager.GET_META_DATA,
        )
        val plugins = mutableListOf<Plugin>()

        for (cr in contentResolvers) {
            try {
                val providerInfo = cr.providerInfo ?: continue
                val authority = providerInfo.authority ?: continue
                val bundle = context.contentResolver.call(
                    Uri.Builder()
                        .scheme("content")
                        .authority(authority)
                        .build(),
                    "getType",
                    null,
                    null,
                ) ?: continue
                val type = bundle.getString("type")
                    ?.let {
                        try {
                            PluginType.valueOf(it)
                        } catch (e: IllegalArgumentException) {
                            null
                        }
                    } ?: continue
                plugins.add(
                    Plugin(
                        label = providerInfo.metaData?.getString("ir.mostafa.launcher.plugin.label")
                            ?: cr.loadLabel(context.packageManager).toString(),
                        description = providerInfo.metaData?.getString("ir.mostafa.launcher.plugin.description"),
                        packageName = providerInfo.packageName,
                        className = providerInfo.name,
                        type = type,
                        authority = authority,
                        enabled = false,
                    )
                )
            } catch (e: SecurityException) {
                CrashReporter.logException(e)
                continue
            } catch (e: Exception) {
                CrashReporter.logException(e)
                continue
            }
        }

        return plugins
    }
}