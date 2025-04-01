package ir.mostafa.launcher.icons.providers

import ir.mostafa.launcher.data.customattrs.LegacyCustomIconPackIcon
import ir.mostafa.launcher.icons.IconPackManager
import ir.mostafa.launcher.icons.LauncherIcon
import ir.mostafa.launcher.search.SavableSearchable

@Deprecated("Use CustomIconPackIconProvider instead")
class LegacyCustomIconPackIconProvider(
    private val customIcon: LegacyCustomIconPackIcon,
    private val iconPackManager: IconPackManager,
) : IconProvider {
    override suspend fun getIcon(searchable: SavableSearchable, size: Int): LauncherIcon? {
        return iconPackManager.getIcon(
            customIcon.iconPackPackage,
            customIcon.iconPackageName,
            customIcon.iconActivityName,
            customIcon.allowThemed,
        )
    }
}