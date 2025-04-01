package ir.mostafa.launcher.preferences.search

import ir.mostafa.launcher.preferences.LauncherDataStore
import ir.mostafa.launcher.preferences.WeightFactor
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class RankingSettings internal constructor(
    private val launcherDataStore: LauncherDataStore,
){
    val weightFactor
        get() = launcherDataStore.data.map { it.rankingWeightFactor }.distinctUntilChanged()

    fun setWeightFactor(weightFactor: WeightFactor) {
        launcherDataStore.update {
            it.copy(rankingWeightFactor = weightFactor)
        }
    }
}