package ir.mostafa.launcher.icons.providers

import android.content.Context
import ir.mostafa.launcher.icons.LauncherIcon
import ir.mostafa.launcher.search.SavableSearchable

class PlaceholderIconProvider(val context: Context) : IconProvider {
    override suspend fun getIcon(searchable: SavableSearchable, size: Int): LauncherIcon {
        return searchable.getPlaceholderIcon(context)
    }
}