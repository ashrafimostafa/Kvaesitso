package ir.mostafa.launcher.badges

import android.content.Context
import ir.mostafa.launcher.badges.providers.AppShortcutBadgeProvider
import ir.mostafa.launcher.badges.providers.BadgeProvider
import ir.mostafa.launcher.badges.providers.CloudBadgeProvider
import ir.mostafa.launcher.badges.providers.HiddenItemBadgeProvider
import ir.mostafa.launcher.badges.providers.NotificationBadgeProvider
import ir.mostafa.launcher.badges.providers.PluginBadgeProvider
import ir.mostafa.launcher.badges.providers.SuspendedAppsBadgeProvider
import ir.mostafa.launcher.badges.providers.ProfileBadgeProvider
import ir.mostafa.launcher.preferences.ui.BadgeSettings
import ir.mostafa.launcher.search.Searchable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

interface BadgeService {
    fun getBadge(searchable: Searchable): Flow<Badge?>
}

internal class BadgeServiceImpl(
    private val context: Context,
    private val settings: BadgeSettings,
) : BadgeService, KoinComponent {

    private val scope = CoroutineScope(Job() + Dispatchers.Default)
    private val badgeProviders = MutableStateFlow<List<BadgeProvider>>(emptyList())

    init {
        scope.launch {
            settings.distinctUntilChanged().collectLatest {
                val providers = mutableListOf<BadgeProvider>()
                providers += ProfileBadgeProvider()
                providers += HiddenItemBadgeProvider()
                if (it.notifications) {
                    providers += NotificationBadgeProvider()
                }
                if (it.cloudFiles) {
                    providers += CloudBadgeProvider(context)
                }
                if (it.shortcuts) {
                    providers += AppShortcutBadgeProvider(context)
                }
                if (it.suspendedApps) {
                    providers += SuspendedAppsBadgeProvider()
                }
                if (it.plugins) {
                    providers += PluginBadgeProvider(context)
                }
                badgeProviders.value = providers
            }
        }
    }

    override fun getBadge(searchable: Searchable): Flow<Badge?> {
        return badgeProviders.flatMapLatest { providers ->
            if (providers.isEmpty()) return@flatMapLatest flowOf(null)
            combine(providers.map { it.getBadge(searchable) }) { it.filterNotNull() }
                .map { it.combine() }
                .flowOn(Dispatchers.Default)
        }
    }

}