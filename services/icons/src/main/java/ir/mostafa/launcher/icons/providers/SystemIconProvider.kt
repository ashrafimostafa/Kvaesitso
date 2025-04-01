package ir.mostafa.launcher.icons.providers

import android.content.Context
import ir.mostafa.launcher.icons.LauncherIcon
import ir.mostafa.launcher.search.SavableSearchable

class SystemIconProvider(
    private val context: Context,
    private val themedIcons: Boolean,
) : IconProvider {
    override suspend fun getIcon(searchable: SavableSearchable, size: Int): LauncherIcon? {
        return searchable.loadIcon(context, size, themedIcons)
    }
}