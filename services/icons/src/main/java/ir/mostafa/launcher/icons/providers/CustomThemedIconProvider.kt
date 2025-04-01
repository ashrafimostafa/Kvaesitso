package ir.mostafa.launcher.icons.providers

import ir.mostafa.launcher.data.customattrs.CustomThemedIcon
import ir.mostafa.launcher.icons.IconPackManager
import ir.mostafa.launcher.icons.LauncherIcon
import ir.mostafa.launcher.search.SavableSearchable

class CustomThemedIconProvider(
    private val customIcon: CustomThemedIcon,
    private val iconPackManager: IconPackManager,
): IconProvider {
    override suspend fun getIcon(searchable: SavableSearchable, size: Int): LauncherIcon? {
        return null //iconPackManager.getThemedIcon(customIcon.iconPackageName)
    }
}