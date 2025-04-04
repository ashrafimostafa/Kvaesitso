package ir.mostafa.launcher.applications

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.pm.LauncherApps
import android.os.Bundle
import android.os.UserHandle
import android.os.UserManager
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.core.content.getSystemService
import ir.mostafa.launcher.icons.ColorLayer
import ir.mostafa.launcher.icons.LauncherIcon
import ir.mostafa.launcher.icons.StaticLauncherIcon
import ir.mostafa.launcher.icons.VectorLayer
import ir.mostafa.launcher.ktx.isAtLeastApiLevel
import ir.mostafa.launcher.search.Application
import ir.mostafa.launcher.search.SavableSearchable
import ir.mostafa.launcher.search.SearchableSerializer

internal data class LockedPrivateProfileApp(
    override val label: String,
    override val componentName: ComponentName,
    override val user: UserHandle,
    internal val userSerialNumber: Long,
): Application {
    override val isSuspended: Boolean = false
    override val versionName: String? = null
    override val canUninstall: Boolean = false

    override val isPrivate: Boolean = true

    override fun uninstall(context: Context) {
        // Do nothing
    }

    override fun openAppDetails(context: Context) {
        // Do nothing
    }

    override val domain: String = LauncherApp.Domain
    override val canShareApk: Boolean = false

    override val key: String = "${domain}://${componentName.packageName}:${componentName.className}:${userSerialNumber}"

    override fun overrideLabel(label: String): SavableSearchable {
        // We don't expose custom labels for locked apps
        return this
    }

    override fun launch(context: Context, options: Bundle?): Boolean {
        if (!isAtLeastApiLevel(35)) return false

        val userManager = context.getSystemService<UserManager>() ?: return false

        if (userManager.isQuietModeEnabled(user)) {
            userManager.requestQuietModeEnabled(false, user)
            return true
        }

        val launcherApps = context.getSystemService<LauncherApps>() ?: return false

        if (isAtLeastApiLevel(31)) {
            options?.putInt("android.activity.splashScreenStyle", 1)
        }

        try {
            launcherApps.startMainActivity(
                componentName,
                user,
                null,
                options
            )
        } catch (e: SecurityException) {
            return false
        } catch (e: ActivityNotFoundException) {
            return false
        }
        return true
    }

    override fun getPlaceholderIcon(context: Context): StaticLauncherIcon {
        return StaticLauncherIcon(
            foregroundLayer = VectorLayer(
                vector = Icons.Rounded.Lock,
            ),
            backgroundLayer = ColorLayer(0)
        )
    }

    override suspend fun loadIcon(context: Context, size: Int, themed: Boolean): LauncherIcon? {
        return null
    }


    override fun getSerializer(): SearchableSerializer {
        return LockedPrivateProfileAppSerializer()
    }
}