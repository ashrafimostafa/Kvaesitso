package ir.mostafa.launcher.appshortcuts

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.content.pm.ShortcutInfo
import android.graphics.drawable.AdaptiveIconDrawable
import android.os.Bundle
import android.os.Process
import android.os.UserHandle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import ir.mostafa.launcher.crashreporter.CrashReporter
import ir.mostafa.launcher.icons.*
import ir.mostafa.launcher.ktx.getSerialNumber
import ir.mostafa.launcher.ktx.isAtLeastApiLevel
import ir.mostafa.launcher.search.AppShortcut
import ir.mostafa.launcher.search.SearchableSerializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.NullPointerException

/**
 * Represents a modern (Android O+) launcher shortcut
 */
internal data class LauncherShortcut(
    val launcherShortcut: ShortcutInfo,
    override val appName: String?,
    internal val userSerialNumber: Long,
    override val labelOverride: String? = null,
) : AppShortcut {

    override val domain: String = Domain
    override val componentName: ComponentName?
        get() = launcherShortcut.activity

    override val packageName: String
        get() = launcherShortcut.`package`

    override val user: UserHandle
        get() = launcherShortcut.userHandle

    constructor(
        context: Context,
        launcherShortcut: ShortcutInfo,
    ): this(
        launcherShortcut = launcherShortcut,
        appName = try {
            context.packageManager.getApplicationInfo(launcherShortcut.`package`, 0)
                .loadLabel(context.packageManager).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            null
        },
        userSerialNumber = launcherShortcut.userHandle.getSerialNumber(context)
    )

    override val label: String
        get() = launcherShortcut.shortLabel?.toString() ?: launcherShortcut.longLabel?.toString() ?: ""

    override fun overrideLabel(label: String): LauncherShortcut {
        return this.copy(labelOverride = label)
    }

    override val preferDetailsOverLaunch: Boolean = false

    private val isMainProfile = launcherShortcut.userHandle == Process.myUserHandle()

    override val key: String
        get() = if (isMainProfile) {
            "$domain://${launcherShortcut.`package`}/${launcherShortcut.id}"
        } else {
            "$domain://${launcherShortcut.`package`}/${launcherShortcut.id}:${userSerialNumber}"
        }

    override fun launch(context: Context, options: Bundle?): Boolean {
        val launcherApps = context.getSystemService<LauncherApps>()!!
        try {
            launcherApps.startShortcut(launcherShortcut, null, options)
        } catch (e: IllegalStateException) {
            return false
        } catch (e: ActivityNotFoundException) {
            return false
        } catch (e: SecurityException) {
            return false
        }
        return true
    }

    override fun getPlaceholderIcon(context: Context): StaticLauncherIcon {
        return StaticLauncherIcon(
            foregroundLayer = TintedIconLayer(
                color = 0xFF3DDA84.toInt(),
                icon = ContextCompat.getDrawable(context, R.drawable.ic_file_android)!!,
                scale = 0.65f,
            ),
            backgroundLayer = ColorLayer(0xFF3DDA84.toInt()),
        )
    }

    override suspend fun loadIcon(
        context: Context,
        size: Int,
        themed: Boolean,
    ): LauncherIcon? {
        val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
        val icon = withContext(Dispatchers.IO) {
            try {
                launcherApps.getShortcutIconDrawable(
                    launcherShortcut,
                    context.resources.displayMetrics.densityDpi
                )
            } catch (e: SecurityException) {
                CrashReporter.logException(e)
                null
            } catch (e: NullPointerException) {
                CrashReporter.logException(e)
                null
            }
        } ?: return null
        if (icon is AdaptiveIconDrawable) {
            if (themed && isAtLeastApiLevel(33) && icon.monochrome != null) {
                return StaticLauncherIcon(
                    foregroundLayer = TintedIconLayer(
                        scale = 1f,
                        icon = icon.monochrome!!,
                    ),
                    backgroundLayer = ColorLayer()
                )
            }
            return StaticLauncherIcon(
                foregroundLayer = icon.foreground?.let {
                    StaticIconLayer(
                        icon = it,
                        scale = 1.5f,
                    )
                } ?: TransparentLayer,
                backgroundLayer = icon.background?.let {
                    StaticIconLayer(
                        icon = it,
                        scale = 1.5f,
                    )
                } ?: TransparentLayer,
            )
        }
        return StaticLauncherIcon(
            foregroundLayer = StaticIconLayer(
                icon = icon,
                scale = 1f
            ),
            backgroundLayer = TransparentLayer
        )
    }

    override fun getSerializer(): SearchableSerializer {
        return LauncherShortcutSerializer()
    }

    override val canDelete: Boolean
        get() = launcherShortcut.isPinned

    override suspend fun delete(context: Context) {
        val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
        if (!launcherApps.hasShortcutHostPermission()) return
        val pinnedShortcutsQuery = LauncherApps.ShortcutQuery().apply {
            setQueryFlags(LauncherApps.ShortcutQuery.FLAG_MATCH_PINNED)
        }
        val userHandle = launcherShortcut.userHandle
        val allPinned = launcherApps.getShortcuts(pinnedShortcutsQuery, userHandle)

        if (allPinned == null) {
            return
        }

        launcherApps.pinShortcuts(
            launcherShortcut.`package`,
            allPinned.filter { it.id != launcherShortcut.id }.map { it.id },
            userHandle
        )
    }

    companion object {
        fun fromPinRequestIntent(context: Context, data: Intent): LauncherShortcut? {
            val launcherApps =
                context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
            val pinRequest = launcherApps.getPinItemRequest(data)
            if (pinRequest == null) {
                return null
            }
            val shortcutInfo = pinRequest.shortcutInfo
            if (shortcutInfo == null) {
                return null
            }
            if (!pinRequest.accept()) {
                return null
            }
            return LauncherShortcut(
                context,
                shortcutInfo,
            )
        }

        const val Domain = "shortcut"
    }

}