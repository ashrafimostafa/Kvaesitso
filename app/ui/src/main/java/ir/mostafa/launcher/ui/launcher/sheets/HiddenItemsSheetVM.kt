package ir.mostafa.launcher.ui.launcher.sheets

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import ir.mostafa.launcher.ui.settings.SettingsActivity

class HiddenItemsSheetVM: ViewModel() {

    fun showHiddenItems(context: Context) {
        context.startActivity(
            Intent(context, SettingsActivity::class.java).apply {
                putExtra(SettingsActivity.EXTRA_ROUTE, "settings/search/hiddenitems")
            }
        )
    }
}