package ir.mostafa.launcher.applications

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import android.os.UserManager
import android.util.Log
import androidx.core.content.getSystemService
import ir.mostafa.launcher.ktx.isAtLeastApiLevel
import ir.mostafa.launcher.search.SavableSearchable
import ir.mostafa.launcher.search.SearchableDeserializer
import ir.mostafa.launcher.search.SearchableSerializer
import org.json.JSONObject

internal class LockedPrivateProfileAppSerializer : SearchableSerializer {
    override fun serialize(searchable: SavableSearchable): String {
        searchable as LockedPrivateProfileApp
        val json = JSONObject()
        json.put("package", searchable.componentName.packageName)
        json.put("activity", searchable.componentName.className)
        json.put("user", searchable.userSerialNumber)
        return json.toString()
    }

    override val typePrefix: String
        get() = "app"
}

class LauncherAppSerializer : SearchableSerializer {
    override fun serialize(searchable: SavableSearchable): String {
        searchable as LauncherApp
        val json = JSONObject()
        json.put("package", searchable.componentName.packageName)
        json.put("activity", searchable.componentName.className)
        json.put("user", searchable.userSerialNumber)
        return json.toString()
    }

    override val typePrefix: String
        get() = "app"
}

class LauncherAppDeserializer(val context: Context) : SearchableDeserializer {
    override suspend fun deserialize(serialized: String): SavableSearchable? {
        val json = JSONObject(serialized)
        val launcherApps = context.getSystemService<LauncherApps>()!!
        val userManager = context.getSystemService<UserManager>()!!
        val userSerial = json.optLong("user")
        val user = userManager.getUserForSerialNumber(userSerial) ?: return null

        val pkg = json.getString("package")
        val activity = json.getString("activity")

        val componentName = ComponentName(pkg, activity)

        if (isAtLeastApiLevel(35)) {
            val launcherUser = launcherApps.getLauncherUserInfo(user) ?: return null
            if (launcherUser.userType == UserManager.USER_TYPE_PROFILE_PRIVATE && userManager.isQuietModeEnabled(user)) {
                return LockedPrivateProfileApp(
                    label = context.getString(R.string.app_label_locked_profile),
                    componentName = componentName,
                    user = user,
                    userSerialNumber = userSerial
                )
            }
        }

        val intent = Intent().also {
            it.component = componentName
        }
        try {
            val launcherActivityInfo = launcherApps.resolveActivity(intent, user) ?: return null
            return LauncherApp(context, launcherActivityInfo)
        } catch (e: SecurityException) {
            return null
        }
    }

}
