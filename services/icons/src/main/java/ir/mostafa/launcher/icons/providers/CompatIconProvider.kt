package ir.mostafa.launcher.icons.providers

import android.content.Context
import android.content.pm.PackageManager
import ir.mostafa.launcher.icons.LauncherIcon
import ir.mostafa.launcher.icons.compat.AdaptiveIconDrawableCompat
import ir.mostafa.launcher.icons.compat.toLauncherIcon
import ir.mostafa.launcher.search.Application
import ir.mostafa.launcher.search.SavableSearchable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CompatIconProvider(
    private val context: Context,
    private val themed: Boolean = false,
) : IconProvider {
    override suspend fun getIcon(searchable: SavableSearchable, size: Int): LauncherIcon? {
        if (searchable !is Application) return null
        val component = searchable.componentName

        val icon = withContext(Dispatchers.IO) {
            val activityInfo = try {
                context.packageManager.getActivityInfo(component, 0)
            } catch (e: PackageManager.NameNotFoundException) {
                return@withContext null
            }
            val iconRes = activityInfo.iconResource
            val resources = try {
                context.packageManager.getResourcesForApplication(activityInfo.packageName)
            } catch (e: PackageManager.NameNotFoundException) {
                return@withContext null
            }
            AdaptiveIconDrawableCompat.from(resources, iconRes)
        } ?: return null

        return icon.toLauncherIcon(themed = themed)
    }
}