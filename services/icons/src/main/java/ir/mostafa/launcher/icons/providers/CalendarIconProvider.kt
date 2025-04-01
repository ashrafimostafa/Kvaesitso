package ir.mostafa.launcher.icons.providers

import android.content.Context
import android.content.pm.PackageManager
import ir.mostafa.launcher.icons.DynamicCalendarIcon
import ir.mostafa.launcher.icons.LauncherIcon
import ir.mostafa.launcher.ktx.obtainTypedArrayOrNull
import ir.mostafa.launcher.search.Application
import ir.mostafa.launcher.search.SavableSearchable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CalendarIconProvider(val context: Context, val themed: Boolean): IconProvider {
    override suspend fun getIcon(searchable: SavableSearchable, size: Int): LauncherIcon? = withContext(Dispatchers.IO) {
        if(searchable !is Application) return@withContext null
        val component = searchable.componentName
        val pm = context.packageManager
        val ai = try {
            pm.getActivityInfo(component, PackageManager.GET_META_DATA)
        } catch (e: PackageManager.NameNotFoundException) {
            return@withContext null
        }
        var arrayId = ai.metaData?.getInt("com.teslacoilsw.launcher.calendarIconArray") ?: 0
        if (arrayId == 0) arrayId = ai.metaData?.getInt("com.google.android.calendar.dynamic_icons")
            ?: return@withContext null
        if (arrayId == 0) arrayId = ai.metaData?.getInt("org.lineageos.etar.dynamic_icons")
            ?: return@withContext null
        if (arrayId == 0) return@withContext null
        val resources = try {
            pm.getResourcesForActivity(component)
        } catch (e: PackageManager.NameNotFoundException) {
            return@withContext null
        }
        val typedArray = resources.obtainTypedArrayOrNull(arrayId) ?: return@withContext null
        if (typedArray.length() != 31) {
            typedArray.recycle()
            return@withContext null
        }
        val drawableIds = IntArray(31)
        for (i in 0 until 31) {
            drawableIds[i] = typedArray.getResourceId(i, 0)
        }
        typedArray.recycle()
        return@withContext DynamicCalendarIcon(
            resources = resources,
            resourceIds = drawableIds,
            isThemed = themed
        )
    }
}