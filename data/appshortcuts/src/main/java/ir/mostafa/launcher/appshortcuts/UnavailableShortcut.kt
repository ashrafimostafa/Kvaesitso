package ir.mostafa.launcher.appshortcuts

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Process
import android.os.UserHandle
import ir.mostafa.launcher.icons.ColorLayer
import ir.mostafa.launcher.icons.StaticLauncherIcon
import ir.mostafa.launcher.icons.TintedIconLayer
import ir.mostafa.launcher.search.AppShortcut
import ir.mostafa.launcher.search.SavableSearchable
import ir.mostafa.launcher.search.SearchableSerializer

internal class UnavailableShortcut(
    override val label: String,
    override val appName: String?,
    override val packageName: String,
    val shortcutId: String,
    val isMainProfile: Boolean,
    override val user: UserHandle,
    val userSerial: Long,
): AppShortcut {

    override val key: String
        get() = if (isMainProfile) {
            "$domain://${packageName}/${shortcutId}"
        } else {
            "$domain://${packageName}/${shortcutId}:$userSerial"
        }

    override val labelOverride: String?
        get() = null
    override val componentName: ComponentName?
        get() = null

    override fun getPlaceholderIcon(context: Context): StaticLauncherIcon {
        return StaticLauncherIcon(
            foregroundLayer = TintedIconLayer(
                icon = context.getDrawable(R.drawable.ic_file_android)!!,
                color = 0xFF333333.toInt()
            ),
            backgroundLayer = ColorLayer(0xFF333333.toInt()),
        )
    }

    override val domain: String
        get() = LauncherShortcut.Domain

    override fun overrideLabel(label: String): SavableSearchable {
        return this
    }

    override fun launch(context: Context, options: Bundle?): Boolean {
        return false
    }

    override fun getSerializer(): SearchableSerializer {
        return UnavailableShortcutSerializer()
    }

    override val isUnavailable: Boolean = true

    companion object {
        internal operator fun invoke(context: Context, id: String, packageName: String, user: UserHandle, userSerial: Long): UnavailableShortcut? {
            val appInfo = try {
                context.packageManager.getApplicationInfo(packageName, 0)
            } catch (e: PackageManager.NameNotFoundException) {
                return null
            }
            return UnavailableShortcut(
                label = context.getString(R.string.shortcut_label_unavailable),
                appName = appInfo.loadLabel(context.packageManager).toString(),
                packageName = packageName,
                shortcutId = id,
                isMainProfile = user == Process.myUserHandle(),
                user = user,
                userSerial = userSerial,
            )
        }
    }
}