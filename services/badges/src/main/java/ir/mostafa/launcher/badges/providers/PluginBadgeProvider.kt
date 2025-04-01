package ir.mostafa.launcher.badges.providers

import android.content.Context
import ir.mostafa.launcher.badges.Badge
import ir.mostafa.launcher.badges.BadgeIcon
import ir.mostafa.launcher.badges.MutableBadge
import ir.mostafa.launcher.search.SavableSearchable
import ir.mostafa.launcher.search.Searchable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class PluginBadgeProvider(private val context: Context): BadgeProvider {
    override fun getBadge(searchable: Searchable): Flow<Badge?> {
        if (searchable !is SavableSearchable) return flowOf(null)
        return flow {
            emit(null)
            val icon = searchable.getProviderIcon(context)
            if (icon != null) {
                emit(MutableBadge(icon = BadgeIcon(icon)))
            }
        }
    }
}