package ir.mostafa.launcher.preferences.ui

import ir.mostafa.launcher.preferences.LauncherDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class IconSettingsData(
    val themedIcons: Boolean,
    val forceThemed: Boolean,
    val adaptify: Boolean,
    val iconPack: String?,
)

class IconSettings internal constructor(
    private val launcherDataStore: LauncherDataStore,
) : Flow<IconSettingsData> by (
        launcherDataStore.data.map {
            IconSettingsData(
                themedIcons = it.iconsThemed,
                forceThemed = it.iconsForceThemed,
                adaptify = it.iconsAdaptify,
                iconPack = it.iconsPack,
            )
        }
        ) {

    fun setAdaptifyLegacyIcons(adaptify: Boolean) {
        launcherDataStore.update {
            it.copy(iconsAdaptify = adaptify)
        }
    }

    fun setThemedIcons(themedIcons: Boolean) {
        launcherDataStore.update {
            it.copy(iconsThemed = themedIcons)
        }
    }

    fun setForceThemedIcons(forceThemed: Boolean) {
        launcherDataStore.update {
            it.copy(iconsForceThemed = forceThemed)
        }
    }

    fun setIconPack(iconPack: String?) {
        launcherDataStore.update {
            it.copy(iconsPack = iconPack)
        }
    }

    fun setIconPackThemed(iconPackThemed: Boolean) {
        launcherDataStore.update {
            it.copy(iconsPackThemed = iconPackThemed)
        }
    }


}