package ir.mostafa.launcher.icons.providers

import ir.mostafa.launcher.icons.LauncherIcon
import ir.mostafa.launcher.search.SavableSearchable

interface IconProvider {
    suspend fun getIcon(searchable: SavableSearchable, size: Int): LauncherIcon?
}

internal suspend fun Iterable<IconProvider>.getFirstIcon(
    searchable: SavableSearchable,
    size: Int
): LauncherIcon? {
    for (provider in this) {
        val icon = provider.getIcon(searchable, size)
        if (icon != null) {
            return icon
        }
    }
    return null
}