package ir.mostafa.launcher.badges.providers

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.HourglassBottom
import ir.mostafa.launcher.badges.Badge
import ir.mostafa.launcher.badges.BadgeIcon
import ir.mostafa.launcher.badges.MutableBadge
import ir.mostafa.launcher.search.Application
import ir.mostafa.launcher.search.Searchable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.koin.core.component.KoinComponent

class SuspendedAppsBadgeProvider : BadgeProvider, KoinComponent {

    override fun getBadge(searchable: Searchable): Flow<Badge?> {
        return if (searchable is Application && searchable.isSuspended) {
            flowOf(MutableBadge(icon = BadgeIcon(Icons.Rounded.HourglassBottom)))
        } else {
            flowOf(null)
        }
    }
}