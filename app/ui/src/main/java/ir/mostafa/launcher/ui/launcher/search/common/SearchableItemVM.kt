package ir.mostafa.launcher.ui.launcher.search.common

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.geometry.Rect
import androidx.core.app.ActivityOptionsCompat
import ir.mostafa.launcher.applications.AppRepository
import ir.mostafa.launcher.appshortcuts.AppShortcutRepository
import ir.mostafa.launcher.badges.BadgeService
import ir.mostafa.launcher.devicepose.DevicePoseProvider
import ir.mostafa.launcher.icons.IconService
import ir.mostafa.launcher.icons.LauncherIcon
import ir.mostafa.launcher.notifications.Notification
import ir.mostafa.launcher.notifications.NotificationRepository
import ir.mostafa.launcher.permissions.PermissionGroup
import ir.mostafa.launcher.permissions.PermissionsManager
import ir.mostafa.launcher.preferences.search.LocationSearchSettings
import ir.mostafa.launcher.search.AppShortcut
import ir.mostafa.launcher.search.Application
import ir.mostafa.launcher.search.File
import ir.mostafa.launcher.search.SavableSearchable
import ir.mostafa.launcher.search.UpdatableSearchable
import ir.mostafa.launcher.search.UpdateResult
import ir.mostafa.launcher.services.favorites.FavoritesService
import ir.mostafa.launcher.services.tags.TagsService
import ir.mostafa.launcher.ui.R
import ir.mostafa.launcher.ui.launcher.search.ListItemViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalCoroutinesApi::class)
class SearchableItemVM : ListItemViewModel(), KoinComponent {
    private val favoritesService: FavoritesService by inject()
    private val badgeService: BadgeService by inject()
    private val iconService: IconService by inject()
    private val tagsService: TagsService by inject()
    private val notificationRepository: NotificationRepository by inject()
    private val appRepository: AppRepository by inject()
    private val appShortcutRepository: AppShortcutRepository by inject()
    private val permissionsManager: PermissionsManager by inject()
    private val locationSearchSettings: LocationSearchSettings by inject()

    val isUpToDate = MutableStateFlow(true)

    val devicePoseProvider: DevicePoseProvider by inject()

    val searchable = MutableStateFlow<SavableSearchable?>(null)
    private val iconSize = MutableStateFlow(0)
    fun init(searchable: SavableSearchable, iconSize: Int) {
        this.searchable.value = searchable
        this.iconSize.value = iconSize
    }

    val isPinned = searchable.flatMapLatest {
        if (it == null) emptyFlow() else favoritesService.isPinned(it)
    }

    fun pin() {
        searchable.value?.let { favoritesService.pinItem(it) }
    }

    fun unpin() {
        searchable.value?.let { favoritesService.unpinItem(it) }
    }

    val badge = searchable.flatMapLatest {
        if (it == null) emptyFlow() else badgeService.getBadge(it)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val icon = searchable.combine(iconSize) { sh, sz -> sh to sz }.flatMapLatest { (s, size) ->
        if (s == null || size == 0) emptyFlow() else iconService.getIcon(s, size)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val tags = searchable.flatMapLatest {
        if (it == null) emptyFlow() else tagsService.getTags(it)
    }

    val notifications = searchable.flatMapLatest { searchable ->
        if (searchable !is Application) emptyFlow()
        else notificationRepository.notifications.map { it.filter { it.packageName == searchable.componentName.packageName && !it.isGroupSummary } }
    }

    val children = searchable.flatMapLatest {
        when(it) {
            is Application -> appShortcutRepository
                .findMany(
                    componentName = it.componentName,
                    user = it.user,
                    manifest = true,
                    dynamic = true,
                )
            is AppShortcut -> {
                val packageName = it.componentName?.packageName ?: return@flatMapLatest flowOf(
                    emptyList()
                )
                appRepository
                    .findOne(
                        packageName = packageName,
                        user = it.user,
                    )
                    .map { listOfNotNull(it) }
            }
            else -> flowOf(
                emptyList()
            )
        }
    }

    fun launch(context: Context, bounds: Rect? = null): Boolean {
        val searchable = searchable.value ?: return false
        val view = (context as? AppCompatActivity)?.window?.decorView
        val options = if (bounds != null && view != null) {
            ActivityOptionsCompat.makeScaleUpAnimation(
                view,
                bounds.left.toInt(),
                bounds.top.toInt(),
                bounds.width.toInt(),
                bounds.height.toInt()
            )
        } else {
            ActivityOptionsCompat.makeBasic()
        }
        val bundle = options.toBundle()
        if (searchable.launch(context, bundle)) {
            favoritesService.reportLaunch(searchable)
            return true
        } else if (searchable is Application || searchable is AppShortcut) {
            favoritesService.reset(searchable)
        }
        return false
    }

    fun clearNotification(notification: Notification) {
        notificationRepository.cancelNotification(notification)
    }

    fun getChildIcon(child: SavableSearchable, size: Int): Flow<LauncherIcon?> {
        return iconService.getIcon(child, size)
    }

    fun isChildPinned(child: SavableSearchable): Flow<Boolean> {
        return favoritesService.isPinned(child)
    }

    fun pinChild(child: SavableSearchable) {
        favoritesService.pinItem(child)
    }

    fun unpinChild(child: SavableSearchable) {
        favoritesService.unpinItem(child)
    }

    fun launchChild(context: Context, child: SavableSearchable) {
        if(child.launch(context, null)) {
            favoritesService.reportLaunch(child)
        }
    }

    fun delete(context: Context) {
        val searchable = searchable.value ?: return
        if (searchable is File) {
            viewModelScope.launch {
                searchable.delete(context.applicationContext)
            }
        }
        if (searchable is AppShortcut) {
            viewModelScope.launch {
                searchable.delete(context.applicationContext)
            }
        }
        favoritesService.reset(searchable)
    }

    private var shouldRetryUpdate = false

    fun requestUpdatedSearchable(context: Context) {
        val searchable = searchable.value ?: return
        if (searchable is UpdatableSearchable<*>) {
            val updatedSelf = searchable.updatedSelf ?: return
            val sinceTimestamp = System.currentTimeMillis() - searchable.timestamp

            val isOutOfDate = 1.minutes.inWholeMilliseconds < sinceTimestamp

            if (!shouldRetryUpdate && !isOutOfDate) return

            viewModelScope.launch {
                with(updatedSelf(searchable)) {
                    when (this) {
                        is UpdateResult.Success -> {
                            isUpToDate.value = true
                            shouldRetryUpdate = false
                            favoritesService.upsert(this.result)
                        }

                        is UpdateResult.TemporarilyUnavailable -> {
                            isUpToDate.value = false
                            shouldRetryUpdate = true
                        }

                        is UpdateResult.PermanentlyUnavailable -> {
                            isUpToDate.value = false
                            shouldRetryUpdate = false
                            favoritesService.delete(searchable)
                            Toast.makeText(
                                context,
                                R.string.unavailable_searchable,
                                Toast.LENGTH_LONG
                            ).show()
                            Log.d("requestUpdatedSearchable", "PermanentlyUnavailable", this.cause)
                        }
                    }
                }
            }
        }
    }

    fun requestShortcutPermission(activity: AppCompatActivity) {
        permissionsManager.requestPermission(activity, PermissionGroup.AppShortcuts)
    }

    val imperialUnits = locationSearchSettings.imperialUnits
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    val showMap = locationSearchSettings.showMap
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    val applyMapTheming = locationSearchSettings.themeMap
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    val mapTileServerUrl = locationSearchSettings.tileServer
        .map { it ?: LocationSearchSettings.DefaultTileServerUrl }
        .stateIn(viewModelScope, SharingStarted.Lazily, "")
}