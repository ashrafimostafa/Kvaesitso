package ir.mostafa.launcher.appshortcuts

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.Intent.ShortcutIconResource
import android.graphics.drawable.AdaptiveIconDrawable
import android.os.Bundle
import android.util.Log
import ir.mostafa.launcher.icons.*
import ir.mostafa.launcher.ktx.getDrawableOrNull
import ir.mostafa.launcher.ktx.isAtLeastApiLevel
import ir.mostafa.launcher.ktx.tryStartActivity
import ir.mostafa.launcher.search.AppShortcut
import ir.mostafa.launcher.search.SearchableSerializer

internal data class LegacyShortcut(
    val intent: Intent,
    override val label: String,
    override val appName: String?,
    val iconResource: ShortcutIconResource?,
    override val labelOverride: String? = null,
) : AppShortcut {

    override val domain = Domain
    override val key: String = "$domain://${intent.toUri(0)}"

    override fun overrideLabel(label: String): LegacyShortcut {
        return this.copy(labelOverride = label)
    }


    override fun launch(context: Context, options: Bundle?): Boolean {
        return context.tryStartActivity(intent, options)
    }

    override val componentName: ComponentName?
        get() = intent.component

    override val packageName: String?
        get() = intent.`package` ?: intent.component?.packageName

    override suspend fun loadIcon(context: Context, size: Int, themed: Boolean): LauncherIcon? {
        if (iconResource == null) return null
        val resources = context.packageManager.getResourcesForApplication(iconResource.packageName)
        val drawableId =
            resources.getIdentifier(iconResource.resourceName, "drawable", iconResource.packageName)
        if (drawableId == 0) return null
        val icon = resources.getDrawableOrNull(drawableId) ?: return null
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
        return LegacyShortcutSerializer()
    }

    companion object {

        const val Domain = "legacyshortcut"

        fun fromPinRequestIntent(context: Context, data: Intent): LegacyShortcut? {
            val intent: Intent? = data.extras?.getParcelable(Intent.EXTRA_SHORTCUT_INTENT)
            val name: String? = data.extras?.getString(Intent.EXTRA_SHORTCUT_NAME)
            val iconResource: ShortcutIconResource? =
                data.extras?.getParcelable(Intent.EXTRA_SHORTCUT_ICON_RESOURCE)

            if (intent == null || name == null) {
                return null
            }

            val packageName = intent.`package` ?: intent.component?.packageName

            return LegacyShortcut(
                intent = intent,
                appName = packageName?.let {
                    context.packageManager.getApplicationInfo(
                        it, 0
                    ).loadLabel(context.packageManager).toString()
                },
                label = name,
                iconResource = iconResource
            )
        }
    }
}