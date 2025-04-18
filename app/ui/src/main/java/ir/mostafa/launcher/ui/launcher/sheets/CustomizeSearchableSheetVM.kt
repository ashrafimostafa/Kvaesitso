package ir.mostafa.launcher.ui.launcher.sheets

import androidx.compose.runtime.mutableStateOf
import ir.mostafa.launcher.data.customattrs.CustomAttributesRepository
import ir.mostafa.launcher.data.customattrs.CustomIcon
import ir.mostafa.launcher.icons.CustomIconWithPreview
import ir.mostafa.launcher.icons.IconPack
import ir.mostafa.launcher.icons.IconService
import ir.mostafa.launcher.icons.LauncherIcon
import ir.mostafa.launcher.search.SavableSearchable
import ir.mostafa.launcher.searchable.VisibilityLevel
import ir.mostafa.launcher.services.favorites.FavoritesService
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.coroutines.coroutineContext

class CustomizeSearchableSheetVM(
    private val searchable: SavableSearchable
) : KoinComponent {
    private val iconService: IconService by inject()
    private val customAttributesRepository: CustomAttributesRepository by inject()
    private val favoritesService: FavoritesService by inject()

    val isIconPickerOpen = mutableStateOf(false)

    fun getIcon(size: Int): Flow<LauncherIcon?> {
        return iconService.getIcon(searchable, size)
    }

    fun getIconSuggestions(size: Int) = flow {
        emit(iconService.getCustomIconSuggestions(searchable, size))
    }

    fun openIconPicker() {
        isIconPickerOpen.value = true
    }

    fun closeIconPicker() {
        isIconPickerOpen.value = false
    }

    fun pickIcon(icon: CustomIcon?) {
        iconService.setCustomIcon(searchable, icon)
        closeIconPicker()
    }

    fun getDefaultIcon(size: Int) = flow {
        emit(iconService.getUncustomizedDefaultIcon(searchable, size))
    }

    val iconSearchResults = mutableStateOf(emptyList<CustomIconWithPreview>())
    val isSearchingIcons = mutableStateOf(false)

    val installedIconPacks = iconService.getInstalledIconPacks()

    private var debounceSearchJob: Job? = null
    suspend fun searchIcon(query: String, iconPack: IconPack?) {
        debounceSearchJob?.cancelAndJoin()
        if (query.isBlank()) {
            iconSearchResults.value = emptyList()
            isSearchingIcons.value = false
            return
        }
        withContext(coroutineContext) {
            debounceSearchJob = launch {
                delay(500)
                isSearchingIcons.value = true
                iconSearchResults.value = emptyList()
                iconSearchResults.value = iconService.searchCustomIcons(query, iconPack)
                isSearchingIcons.value = false
            }
        }
    }

    fun setCustomLabel(label: String) {
        if (label.isBlank()) {
            customAttributesRepository.clearCustomLabel(searchable)
        } else {
            customAttributesRepository.setCustomLabel(searchable, label)
        }
    }

    fun setTags(tags: List<String>) {
        customAttributesRepository.setTags(searchable, tags)
    }

    fun setVisibility(visibility: VisibilityLevel) {
        favoritesService.setVisibility(searchable, visibility)
    }

    fun getTags(): Flow<List<String>> {
        return customAttributesRepository.getTags(searchable)
    }

    fun getVisibility(): Flow<VisibilityLevel> {
        return favoritesService.getVisibility(searchable)
    }

    suspend fun autocompleteTags(query: String): List<String> {
        return customAttributesRepository.getAllTags(startsWith = query).first()
    }
}