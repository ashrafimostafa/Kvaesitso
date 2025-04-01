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

internal class DynamicCalendarIcon(
    val resources: Resources,
    val resourceIds: IntArray,
    val isThemed: Boolean = false,
    private var transformations: List<LauncherIconTransformation> = emptyList(),
) : DynamicLauncherIcon, TransformableDynamicLauncherIcon {

    init {
        if (resourceIds.size < 31) throw IllegalArgumentException("DynamicCalendarIcon resourceIds must at least have 31 items")
    }

    override suspend fun getIcon(time: Long): StaticLauncherIcon = withContext(Dispatchers.IO) {
        val day = Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).dayOfMonth
        val resId = resourceIds[day - 1]

        val adaptiveIcon = AdaptiveIconDrawableCompat.from(resources, resId)

        var icon = if (adaptiveIcon != null) {
            adaptiveIcon.toLauncherIcon(themed = isThemed)
        } else {
            try {
                val drawable = ResourcesCompat.getDrawable(resources, resId, null)

                when {
                    drawable is AdaptiveIconDrawable -> AdaptiveIconDrawableCompat.from(drawable).toLauncherIcon(themed = isThemed)
                    drawable != null -> StaticLauncherIcon(
                        foregroundLayer = StaticIconLayer(
                            icon = drawable,
                            scale = 1f,
                        ),
                        backgroundLayer = TransparentLayer,
                    )
                    else -> null
                }
            } catch (e: Resources.NotFoundException) {
                null
            } ?: return@withContext StaticLauncherIcon(
                foregroundLayer = TextLayer(day.toString()),
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