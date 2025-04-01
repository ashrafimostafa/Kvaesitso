package ir.mostafa.launcher.badges.providers

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Work
import ir.mostafa.launcher.badges.Badge
import ir.mostafa.launcher.badges.BadgeIcon
import ir.mostafa.launcher.icons.PrivateSpace
import ir.mostafa.launcher.profiles.Profile
import ir.mostafa.launcher.profiles.ProfileManager
import ir.mostafa.launcher.search.AppShortcut
import ir.mostafa.launcher.search.Application
import ir.mostafa.launcher.search.Searchable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ProfileBadgeProvider : BadgeProvider, KoinComponent {
    private val profileManager: ProfileManager by inject()

    override fun getBadge(searchable: Searchable): Flow<Badge?> = flow {
        val userHandle = when(searchable) {
            is Application -> searchable.user
            is AppShortcut -> searchable.user
            else -> null
        }
        if (userHandle != null) {
            emitAll(
                profileManager.getProfile(userHandle).map {
                    when(it?.type) {
                        Profile.Type.Work -> WorkProfile
                        Profile.Type.Private -> PrivateProfile
                        else -> null
                    }
                }
            )
        } else {
            emit(null)
        }
    }

    companion object {
        private val WorkProfile = Badge(
            icon = BadgeIcon(Icons.Rounded.Work)
        )

        private val PrivateProfile = Badge(
            icon = BadgeIcon(Icons.Rounded.PrivateSpace)
        )
    }
}