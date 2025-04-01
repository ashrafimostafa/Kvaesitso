package ir.mostafa.launcher.badges.providers

import android.content.Context
import android.content.pm.PackageManager
import ir.mostafa.launcher.badges.Badge
import ir.mostafa.launcher.badges.BadgeIcon
import ir.mostafa.launcher.badges.MutableBadge
import ir.mostafa.launcher.graphics.BadgeDrawable
import ir.mostafa.launcher.search.AppShortcut
import ir.mostafa.launcher.search.Searchable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext

class AppShortcutBadgeProvider(
    private val context: Context
) : BadgeProvider {
    override fun getBadge(searchable: Searchable): Flow<Badge?> = channelFlow {
        if (searchable is AppShortcut) {
            val componentName = searchable.componentName
            val packageName = searchable.packageName
            if (componentName != null) {
                withContext(Dispatchers.IO) {
                    val icon = try {
                        context.packageManager.getActivityIcon(
                            componentName
                        )
                    } catch (e: PackageManager.NameNotFoundException) {
                        return@withContext
                    }
                    val badge = MutableBadge(icon = BadgeIcon(BadgeDrawable(context, icon)))
                    send(badge)
                }
            } else if (packageName != null) {
                withContext(Dispatchers.IO) {
                    val icon = try {
                        context.packageManager.getApplicationIcon(
                            packageName
                        )
                    } catch (e: PackageManager.NameNotFoundException) {
                        return@withContext
                    }
                    val badge = MutableBadge(icon = BadgeIcon(BadgeDrawable(context, icon)))
                    send(badge)
                }
            } else {
                send(null)
                return@channelFlow
            }
        } else {
            send(null)
        }
    }
}