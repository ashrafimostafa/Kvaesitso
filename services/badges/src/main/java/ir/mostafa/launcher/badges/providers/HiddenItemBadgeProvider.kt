package ir.mostafa.launcher.badges.providers

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.VisibilityOff
import ir.mostafa.launcher.badges.Badge
import ir.mostafa.launcher.badges.BadgeIcon
import ir.mostafa.launcher.search.SavableSearchable
import ir.mostafa.launcher.search.Searchable
import ir.mostafa.launcher.searchable.SavableSearchableRepository
import ir.mostafa.launcher.searchable.VisibilityLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HiddenItemBadgeProvider(
) : BadgeProvider, KoinComponent {

    private val searchableRepository: SavableSearchableRepository by inject()

    private val scope = CoroutineScope(Job() + Dispatchers.Default)

    private val hiddenItemKeys = searchableRepository.getKeys(
        maxVisibility = VisibilityLevel.Hidden,
        limit = 9999,
    ).shareIn(scope, SharingStarted.WhileSubscribed(), 1)

    override fun getBadge(searchable: Searchable): Flow<Badge?> {
        if (searchable !is SavableSearchable) return flowOf(null)
        return hiddenItemKeys.map { keys ->
            if (searchable.key in keys) {
                Badge(
                    icon = BadgeIcon(Icons.Rounded.VisibilityOff)
                )
            } else {
                null
            }
        }
    }
}