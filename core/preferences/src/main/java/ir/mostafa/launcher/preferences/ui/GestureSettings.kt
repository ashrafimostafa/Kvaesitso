package ir.mostafa.launcher.preferences.ui

import ir.mostafa.launcher.preferences.GestureAction
import ir.mostafa.launcher.preferences.LauncherDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

data class GestureSettingsData(
    val swipeDown: GestureAction,
    val swipeLeft: GestureAction,
    val swipeRight: GestureAction,
    val doubleTap: GestureAction,
    val longPress: GestureAction,
    val homeButton: GestureAction,
)

class GestureSettings internal constructor(
    private val dataStore: LauncherDataStore,
): Flow<GestureSettingsData> by (
    dataStore.data.map {
        GestureSettingsData(
            swipeDown = it.gesturesSwipeDown,
            swipeLeft = it.gesturesSwipeLeft,
            swipeRight = it.gesturesSwipeRight,
            doubleTap = it.gesturesDoubleTap,
            longPress = it.gesturesLongPress,
            homeButton = it.gesturesHomeButton,
        )
    }.distinctUntilChanged()
) {
    val swipeDown: Flow<GestureAction> = dataStore.data.map { it.gesturesSwipeDown }
        .distinctUntilChanged()

    val swipeLeft: Flow<GestureAction> = dataStore.data.map { it.gesturesSwipeLeft }
        .distinctUntilChanged()

    val swipeRight: Flow<GestureAction> = dataStore.data.map { it.gesturesSwipeRight }
        .distinctUntilChanged()

    val doubleTap: Flow<GestureAction> = dataStore.data.map { it.gesturesDoubleTap }
        .distinctUntilChanged()

    val longPress: Flow<GestureAction> = dataStore.data.map { it.gesturesLongPress }
        .distinctUntilChanged()

    val homeButton: Flow<GestureAction> = dataStore.data.map { it.gesturesHomeButton }
        .distinctUntilChanged()

    fun setSwipeDown(action: GestureAction) {
        dataStore.update {
            it.copy(gesturesSwipeDown = action)
        }
    }

    fun setSwipeLeft(action: GestureAction) {
        dataStore.update {
            it.copy(gesturesSwipeLeft = action)
        }
    }

    fun setSwipeRight(action: GestureAction) {
        dataStore.update {
            it.copy(gesturesSwipeRight = action)
        }
    }

    fun setDoubleTap(action: GestureAction) {
        dataStore.update {
            it.copy(gesturesDoubleTap = action)
        }
    }

    fun setLongPress(action: GestureAction) {
        dataStore.update {
            it.copy(gesturesLongPress = action)
        }
    }

    fun setHomeButton(action: GestureAction) {
        dataStore.update {
            it.copy(gesturesHomeButton = action)
        }
    }


}