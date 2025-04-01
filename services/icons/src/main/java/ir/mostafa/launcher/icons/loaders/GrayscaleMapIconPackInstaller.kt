package ir.mostafa.launcher.icons.loaders

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.LayerDrawable
import android.util.Log
import ir.mostafa.launcher.crashreporter.CrashReporter
import ir.mostafa.launcher.database.AppDatabase
import ir.mostafa.launcher.icons.AppIcon
import ir.mostafa.launcher.icons.CalendarIcon
import ir.mostafa.launcher.icons.ClockIcon
import ir.mostafa.launcher.icons.IconPack
import ir.mostafa.launcher.icons.compat.ClockIconConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser

class GrayscaleMapIconPackInstaller(
    private val context: Context,
    database: AppDatabase,
) : IconPackInstaller(database) {
    private val SUPPORTED_GRAYSCALE_MAP_PROVIDERS = arrayOf(
        "app.lawnchair.lawnicons", // Lawnicons
    )

    override suspend fun IconPackInstallerScope.buildIconPack(iconPack: IconPack) {
        withContext(Dispatchers.IO) {
            try {
                val packageName = iconPack.packageName
                val resources = context.packageManager.getResourcesForApplication(packageName)
                val resId = resources.getIdentifier("grayscale_icon_map", "xml", packageName)
                if (resId == 0) {
                    return@withContext
                }
                val parser = resources.getXml(resId)
                loop@ while (parser.next() != XmlPullParser.END_DOCUMENT) {
                    if (parser.eventType != XmlPullParser.START_TAG) continue
                    if (parser.name == "icon") {
                        val pkg = parser.getAttributeValue(null, "package") ?: continue@loop
                        val drawableRes =
                            parser.getAttributeResourceValue(null, "drawable", 0)

                        val type = try {
                            resources.getResourceTypeName(drawableRes)
                        } catch (e: Resources.NotFoundException) {
                            continue@loop
                        }

                        if (type == "drawable") {
                            val drawableName =
                                resources.getResourceEntryNameOrNull(drawableRes) ?: continue@loop
                            val icon = AppIcon(
                                drawable = drawableName,
                                packageName = pkg,
                                activityName = null,
                                iconPack = packageName,
                                name = null,
                                themed = true
                            )
                            addIcon(icon)
                        } else if (type == "array") {
                            val array = resources.obtainTypedArray(drawableRes)
                            if (array.length() == 31) {
                                val drawables = mutableListOf<String>()
                                for (i in 0 until 31) {
                                    val res = array.getResourceId(i, 0)
                                    if (res == 0) break
                                    val drawableName =
                                        resources.getResourceEntryNameOrNull(res) ?: break
                                    drawables.add(drawableName)
                                }
                                if (drawables.size == 31) {
                                    addIcon(
                                        CalendarIcon(
                                            drawables = drawables,
                                            packageName = pkg,
                                            iconPack = packageName,
                                            themed = true
                                        )
                                    )
                                }
                            } else {
                                var i = 0
                                var drawable: LayerDrawable? = null
                                var drawableName: String? = null
                                var minuteIndex: Int? = null
                                var defaultMinute = 0
                                var hourIndex: Int? = null
                                var defaultHour = 0
                                while (i < array.length()) {
                                    when (array.getString(i)) {
                                        "com.android.launcher3.LEVEL_PER_TICK_ICON_ROUND" -> {
                                            i++
                                            drawable = array.getDrawable(i) as? LayerDrawable
                                            drawableName =
                                                array.getResourceId(i, 0).takeIf { it != 0 }
                                                    ?.let { resources.getResourceEntryNameOrNull(it) }
                                        }

                                        "com.android.launcher3.HOUR_LAYER_INDEX" -> {
                                            i++
                                            hourIndex = array.getInt(i, -1).takeIf { it != -1 }
                                        }

                                        "com.android.launcher3.MINUTE_LAYER_INDEX" -> {
                                            i++
                                            minuteIndex = array.getInt(i, -1).takeIf { it != -1 }
                                        }

                                        "com.android.launcher3.DEFAULT_HOUR" -> {
                                            i++
                                            defaultHour = array.getInt(i, 0)
                                        }

                                        "com.android.launcher3.DEFAULT_MINUTE" -> {
                                            i++
                                            defaultMinute = array.getInt(i, 0)
                                        }
                                    }
                                    i++
                                }
                                if (drawable != null && drawableName != null && minuteIndex != null && hourIndex != null) {
                                    addIcon(
                                        ClockIcon(
                                            drawable = drawableName,
                                            packageName = pkg,
                                            config = ClockIconConfig(
                                                hourLayer = hourIndex,
                                                minuteLayer = minuteIndex,
                                                defaultHour = defaultHour,
                                                defaultMinute = defaultMinute,
                                                defaultSecond = 0,
                                                secondLayer = -1,
                                            ),
                                            iconPack = packageName,
                                            themed = true,
                                        )
                                    )
                                }
                            }
                            array.recycle()
                        }
                    }
                }
            } catch (e: PackageManager.NameNotFoundException) {
                CrashReporter.logException(e)
            }
        }
    }

    override fun getInstalledIconPacks(): List<IconPack> {
        val pm = context.packageManager
        return SUPPORTED_GRAYSCALE_MAP_PROVIDERS.mapNotNull {
            try {
                val packageInfo = pm.getPackageInfo(it, 0)
                IconPack(
                    context = context,
                    packageInfo = packageInfo,
                    themed = true,
                )
            } catch (e: PackageManager.NameNotFoundException) {
                null
            }
        }
    }
}

internal fun Resources.getResourceEntryNameOrNull(res: Int): String? {
    return try {
        getResourceEntryName(res)
    } catch (e: Resources.NotFoundException) {
        null
    }
}