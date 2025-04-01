package ir.mostafa.launcher.icons.providers

import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import androidx.core.content.getSystemService
import ir.mostafa.launcher.icons.*
import ir.mostafa.launcher.search.Application
import ir.mostafa.launcher.search.SavableSearchable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class IconPackIconProvider(
    private val context: Context,
    private val iconPack: IconPack,
    private val iconPackManager: IconPackManager,
    private val allowThemed: Boolean,
): IconProvider {
    override suspend fun getIcon(searchable: SavableSearchable, size: Int): LauncherIcon? {
        if (searchable !is Application) return null


        return iconPackManager.getIcon(iconPack.packageName, searchable.componentName.packageName, searchable.componentName.className, allowThemed)
            ?: iconPackManager.generateIcon(
                context,
                iconPack.packageName,
                baseIcon = withContext(Dispatchers.IO) {
                    val ai = context.getSystemService<LauncherApps>()?.resolveActivity(
                        Intent().setComponent(searchable.componentName),
                        searchable.user
                    )
                    ai?.getIcon(context.resources.displayMetrics.densityDpi)
                } ?: return null,
                size = size,
            )
    }
}