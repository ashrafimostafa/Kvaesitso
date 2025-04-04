package ir.mostafa.launcher.appshortcuts

import android.content.ComponentName
import android.content.Context
import android.content.pm.LauncherApps
import android.content.pm.ShortcutInfo
import android.os.Handler
import android.os.Looper
import android.os.Process
import android.os.UserHandle
import androidx.core.content.getSystemService
import ir.mostafa.launcher.ktx.normalize
import ir.mostafa.launcher.permissions.PermissionGroup
import ir.mostafa.launcher.permissions.PermissionsManager
import ir.mostafa.launcher.preferences.search.ShortcutSearchSettings
import ir.mostafa.launcher.search.AppShortcut
import ir.mostafa.launcher.search.SearchableRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.withContext
import org.apache.commons.text.similarity.FuzzyScore
import java.util.Locale

interface AppShortcutRepository : SearchableRepository<AppShortcut> {

    fun findMany(
        componentName: ComponentName? = null,
        user: UserHandle = Process.myUserHandle(),
        manifest: Boolean = false,
        dynamic: Boolean = false,
        pinned: Boolean = false,
        cached: Boolean = false,
        limit: Int = 5,
    ): Flow<ImmutableList<AppShortcut>>

    suspend fun getShortcutsConfigActivities(): List<AppShortcutConfigActivity>
}

internal class AppShortcutRepositoryImpl(
    private val context: Context,
    private val permissionsManager: PermissionsManager,
    private val settings: ShortcutSearchSettings,
) : AppShortcutRepository {

    private val scope = CoroutineScope(Dispatchers.Default + Job())

    override fun findMany(
        componentName: ComponentName?,
        user: UserHandle,
        manifest: Boolean,
        dynamic: Boolean,
        pinned: Boolean,
        cached: Boolean,
        limit: Int
    ): Flow<ImmutableList<AppShortcut>> = flow {
        val shortcuts = withContext(Dispatchers.IO) {
            val launcherApps = context.getSystemService<LauncherApps>()!!
            if (!launcherApps.hasShortcutHostPermission()) return@withContext emptyList()
            val query = LauncherApps.ShortcutQuery()
                .setActivity(componentName)
                .setQueryFlags(
                    buildQueryFlags(manifest, dynamic, pinned, cached)
                )
            val shortcuts = try {
                launcherApps.getShortcuts(query, user)
            } catch (e: IllegalStateException) {
                emptyList()
            }
            val appShortcuts = mutableListOf<LauncherShortcut>()
            appShortcuts.addAll(shortcuts
                ?.let {
                    if (it.size > limit) it.subList(0, limit)
                    else it
                }
                ?.map {
                    LauncherShortcut(
                        context,
                        it,
                    )
                } ?: emptyList()
            )
            appShortcuts
        }
        emit(shortcuts.toImmutableList())
    }

    private fun buildQueryFlags(
        manifest: Boolean,
        dynamic: Boolean,
        pinned: Boolean,
        cached: Boolean,
    ): Int {
        var flags = 0
        if (manifest) flags = flags or LauncherApps.ShortcutQuery.FLAG_MATCH_MANIFEST
        if (dynamic) flags = flags or LauncherApps.ShortcutQuery.FLAG_MATCH_DYNAMIC
        if (pinned) flags = flags or LauncherApps.ShortcutQuery.FLAG_MATCH_PINNED
        if (cached) flags = flags or LauncherApps.ShortcutQuery.FLAG_MATCH_CACHED
        return flags
    }

    override fun search(query: String, allowNetwork: Boolean): Flow<ImmutableList<AppShortcut>> {
        if (query.length < 3) {
            return flowOf(persistentListOf())
        }

        return combine(
            listOf(
                settings.enabled,
                permissionsManager.hasPermission(PermissionGroup.AppShortcuts),
                shortcutChangeEmitter
            )
        ) { it }
            .map { (enabled, perm, _) ->
                enabled as Boolean
                perm as Boolean

                if (enabled && perm) {
                    val launcherApps =
                        context.getSystemService<LauncherApps>() ?: return@map persistentListOf()


                    val shortcutQuery = LauncherApps.ShortcutQuery()
                    shortcutQuery.setQueryFlags(
                        LauncherApps.ShortcutQuery.FLAG_MATCH_PINNED or
                                LauncherApps.ShortcutQuery.FLAG_MATCH_DYNAMIC or
                                LauncherApps.ShortcutQuery.FLAG_MATCH_MANIFEST or
                                LauncherApps.ShortcutQuery.FLAG_MATCH_CACHED or
                                LauncherApps.ShortcutQuery.FLAG_MATCH_PINNED_BY_ANY_LAUNCHER
                    )
                    val shortcuts = launcherApps.getShortcuts(shortcutQuery, Process.myUserHandle())
                        ?.filter {
                            if (it.longLabel != null) {
                                return@filter matches(it.longLabel.toString(), query)
                            }
                            if (it.shortLabel != null) {
                                return@filter matches(it.shortLabel.toString(), query)
                            }
                            return@filter false
                        } ?: emptyList()

                    shortcuts.mapNotNull {
                        LauncherShortcut(
                            context,
                            it
                        )
                    }.toImmutableList()

                } else {
                    persistentListOf()
                }
            }
    }

    private val shortcutChangeEmitter = callbackFlow {
        send(Unit)
        val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps

        val callback = object : LauncherApps.Callback() {
            override fun onPackageRemoved(packageName: String?, user: UserHandle?) {
            }

            override fun onPackageAdded(packageName: String?, user: UserHandle?) {
            }

            override fun onPackageChanged(packageName: String?, user: UserHandle?) {
            }

            override fun onPackagesAvailable(
                packageNames: Array<out String>?,
                user: UserHandle?,
                replacing: Boolean
            ) {
            }

            override fun onPackagesUnavailable(
                packageNames: Array<out String>?,
                user: UserHandle?,
                replacing: Boolean
            ) {
            }

            override fun onShortcutsChanged(
                packageName: String,
                shortcuts: MutableList<ShortcutInfo>,
                user: UserHandle
            ) {
                super.onShortcutsChanged(packageName, shortcuts, user)
                trySend(Unit)
            }

        }

        launcherApps.registerCallback(callback, Handler(Looper.getMainLooper()))

        awaitClose {
            launcherApps.unregisterCallback(callback)
        }
    }.shareIn(scope, SharingStarted.WhileSubscribed(500), 1)

    override suspend fun getShortcutsConfigActivities(): List<AppShortcutConfigActivity> {
        val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
        if (!launcherApps.hasShortcutHostPermission()) return emptyList()
        val results = mutableListOf<AppShortcutConfigActivity>()
        val profiles = launcherApps.profiles
        for (profile in profiles) {
            val activities = launcherApps.getShortcutConfigActivityList(null, profile)
            results.addAll(
                activities.map {
                    AppShortcutConfigActivity(it)
                }
            )
        }
        return results.sorted()
    }


    private fun matches(label: String, query: String): Boolean {
        val normalizedLabel = label.normalize()
        val normalizedQuery = query.normalize()
        if (normalizedLabel.contains(normalizedQuery)) return true
        val fuzzyScore = FuzzyScore(Locale.getDefault())
        return fuzzyScore.fuzzyScore(normalizedLabel, normalizedQuery) >= query.length * 1.5
    }
}