package ir.mostafa.launcher.preferences.media

import ir.mostafa.launcher.preferences.LauncherDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class MediaSettingsData(
    val allowList: Set<String>,
    val denyList: Set<String>,
)

class MediaSettings internal constructor(
    private val launcherDataStore: LauncherDataStore
) : Flow<MediaSettingsData> by (launcherDataStore.data.map {
    MediaSettingsData(
        it.mediaAllowList,
        it.mediaDenyList
    )
}) {
    val allowList
        get() = launcherDataStore.data.map { it.mediaAllowList }

    fun setLists(allowList: Set<String>, denyList: Set<String>) {
        launcherDataStore.update {
            it.copy(mediaAllowList = allowList)
        }
    }

    val denyList
        get() = launcherDataStore.data.map { it.mediaDenyList }
}