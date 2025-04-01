package ir.mostafa.launcher.appshortcuts

import android.content.Context
import android.content.Intent
import ir.mostafa.launcher.search.AppShortcut


fun AppShortcut(context: Context, pinRequestIntent: Intent): AppShortcut? {
    return LauncherShortcut.fromPinRequestIntent(context, pinRequestIntent)
        ?: LegacyShortcut.fromPinRequestIntent(context, pinRequestIntent)
}