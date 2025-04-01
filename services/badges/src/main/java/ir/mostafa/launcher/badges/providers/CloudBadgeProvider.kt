package ir.mostafa.launcher.badges.providers

import android.content.Context
import ir.mostafa.launcher.badges.Badge
import ir.mostafa.launcher.badges.BadgeIcon
import ir.mostafa.launcher.badges.MutableBadge
import ir.mostafa.launcher.search.File
import ir.mostafa.launcher.search.Searchable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class CloudBadgeProvider(
    private val context: Context
): BadgeProvider {
    override fun getBadge(searchable: Searchable): Flow<Badge?> {
        if (searchable is File) {
            val iconResId = searchable.providerIconRes
            if (iconResId != null) {
                return flowOf(MutableBadge(icon = BadgeIcon(context.getDrawable(iconResId)!!)))
            }
        }
        return flowOf(null)
    }
}