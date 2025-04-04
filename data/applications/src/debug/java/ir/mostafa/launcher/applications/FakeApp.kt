package ir.mostafa.launcher.applications

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.os.Process
import android.os.UserHandle
import ir.mostafa.launcher.search.Application
import ir.mostafa.launcher.search.NullSerializer
import ir.mostafa.launcher.search.SavableSearchable
import ir.mostafa.launcher.search.SearchableSerializer

class FakeApp: Application {
    override val componentName: ComponentName = ComponentName(randomString(), randomString())
    override val isSuspended: Boolean = false
    override val user: UserHandle = Process.myUserHandle()
    override val versionName: String = "1.0"
    override val canUninstall: Boolean = false

    override fun uninstall(context: Context) {

    }

    override fun openAppDetails(context: Context) {

    }

    override val canShareApk: Boolean = false
    override val key: String = "fake://${randomString()}"
    override val label: String = randomString()

    override fun overrideLabel(label: String): SavableSearchable {
        return this
    }

    override fun launch(context: Context, options: Bundle?): Boolean {
        return false
    }

    override val domain: String
        get() = "fake"

    override fun getSerializer(): SearchableSerializer {
        return NullSerializer()
    }

    private companion object {
        private fun randomString(): String {
            val charset = "abcdefghijklmnopqrstuvwxyz"
            return (1..10)
                .map { charset.random() }
                .joinToString("")
        }
    }
}