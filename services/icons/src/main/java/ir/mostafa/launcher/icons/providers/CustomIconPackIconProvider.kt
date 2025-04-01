package ir.mostafa.launcher.icons.providers

import ir.mostafa.launcher.data.customattrs.CustomIconPackIcon
import ir.mostafa.launcher.database.entities.IconEntity
import ir.mostafa.launcher.icons.IconPackAppIcon
import ir.mostafa.launcher.icons.IconPackManager
import ir.mostafa.launcher.icons.LauncherIcon
import ir.mostafa.launcher.search.SavableSearchable

class CustomIconPackIconProvider(
    private val customIcon: CustomIconPackIcon,
    private val iconPackManager: IconPackManager,
) : IconProvider {
    override suspend fun getIcon(searchable: SavableSearchable, size: Int): LauncherIcon? {
        val ent = IconEntity(
            type = customIcon.type,
            drawable = customIcon.drawable,
            extras = customIcon.extras,
            iconPack = customIcon.iconPackPackage,
            themed = customIcon.allowThemed,
        )
        val icon = IconPackAppIcon(ent) ?: return null
        return iconPackManager.getIcon(
            customIcon.iconPackPackage,
            icon,
            customIcon.allowThemed,
        )
    }
}