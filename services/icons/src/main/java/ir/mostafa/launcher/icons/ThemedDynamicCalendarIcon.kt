package ir.mostafa.launcher.icons

import android.content.res.Resources
import android.graphics.drawable.AdaptiveIconDrawable
import androidx.core.content.res.ResourcesCompat
import ir.mostafa.launcher.icons.compat.AdaptiveIconDrawableCompat
import ir.mostafa.launcher.icons.compat.toLauncherIcon
import ir.mostafa.launcher.icons.transformations.LauncherIconTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneId

internal class ThemedDynamicCalendarIcon(
    val resources: Resources,
    val resourceIds: IntArray,
    private var transformations: List<LauncherIconTransformation> = emptyList(),
) : DynamicLauncherIcon, TransformableDynamicLauncherIcon {
    override suspend fun getIcon(time: Long): StaticLauncherIcon = withContext(Dispatchers.IO) {
        val day = Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).dayOfMonth
        val resId = resourceIds[day - 1]

        val adaptiveIconCompat = AdaptiveIconDrawableCompat.from(resources, resId)

        if (adaptiveIconCompat != null) {
            var icon = adaptiveIconCompat.toLauncherIcon(themed = true)
            for (transformation in transformations) {
                icon = transformation.transform(icon)
            }
            return@withContext icon
        }

        val drawable = try {
            ResourcesCompat.getDrawable(resources, resId, null)
        } catch (e: Resources.NotFoundException) {
            null
        } ?: return@withContext StaticLauncherIcon(
            foregroundLayer = TextLayer(day.toString()),
            backgroundLayer = ColorLayer()
        )

        var icon = when (drawable) {
            is AdaptiveIconDrawable -> StaticLauncherIcon(
                foregroundLayer = TintedIconLayer(
                    icon = drawable.foreground,
                    scale = 1.5f,
                ),
                backgroundLayer = ColorLayer()
            )

            else -> StaticLauncherIcon(
                foregroundLayer = TintedIconLayer(
                    icon = drawable,
                    scale = 0.65f,
                ),
                backgroundLayer = ColorLayer()
            )
        }


        for (transformation in transformations) {
            icon = transformation.transform(icon)
        }
        return@withContext icon
    }

    override fun setTransformations(transformations: List<LauncherIconTransformation>) {
        this.transformations = transformations
    }
}