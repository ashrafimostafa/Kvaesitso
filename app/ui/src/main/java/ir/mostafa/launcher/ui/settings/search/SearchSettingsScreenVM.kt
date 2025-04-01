package ir.mostafa.launcher.ui.settings.search

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.mostafa.launcher.permissions.PermissionGroup
import ir.mostafa.launcher.permissions.PermissionsManager
import ir.mostafa.launcher.preferences.SearchResultOrder
import ir.mostafa.launcher.preferences.search.CalculatorSearchSettings
import ir.mostafa.launcher.preferences.search.CalendarSearchSettings
import ir.mostafa.launcher.preferences.search.ContactSearchSettings
import ir.mostafa.launcher.preferences.search.LocationSearchSettings
import ir.mostafa.launcher.preferences.search.SearchFilterSettings
import ir.mostafa.launcher.preferences.search.ShortcutSearchSettings
import ir.mostafa.launcher.preferences.search.UnitConverterSettings
import ir.mostafa.launcher.preferences.search.WebsiteSearchSettings
import ir.mostafa.launcher.preferences.search.WikipediaSearchSettings
import ir.mostafa.launcher.preferences.ui.SearchUiSettings
import ir.mostafa.launcher.search.SearchFilters
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SearchSettingsScreenVM : ViewModel(), KoinComponent {
    private val searchUiSettings: SearchUiSettings by inject()
    private val contactSearchSettings: ContactSearchSettings by inject()
    private val calendarSearchSettings: CalendarSearchSettings by inject()
    private val shortcutSearchSettings: ShortcutSearchSettings by inject()
    private val wikipediaSearchSettings: WikipediaSearchSettings by inject()
    private val websiteSearchSettings: WebsiteSearchSettings by inject()
    private val unitConverterSettings: UnitConverterSettings by inject()
    private val calculatorSearchSettings: CalculatorSearchSettings by inject()
    private val searchFilterSettings: SearchFilterSettings by inject()

    private val permissionsManager: PermissionsManager by inject()
    private val locationSearchSettings: LocationSearchSettings by inject()

    val favorites = searchUiSettings.favorites
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun setFavorites(favorites: Boolean) {
        searchUiSettings.setFavorites(favorites)
    }


    val hasContactsPermission = permissionsManager.hasPermission(PermissionGroup.Contacts)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
    val contacts = contactSearchSettings.enabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun setContacts(contacts: Boolean) {
        contactSearchSettings.setEnabled(contacts)
    }

    fun requestContactsPermission(activity: AppCompatActivity) {
        permissionsManager.requestPermission(activity, PermissionGroup.Contacts)
    }

    val calculator = calculatorSearchSettings.enabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun setCalculator(calculator: Boolean) {
        calculatorSearchSettings.setEnabled(calculator)
    }

    val unitConverter = unitConverterSettings.enabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun setUnitConverter(unitConverter: Boolean) {
        unitConverterSettings.setEnabled(unitConverter)
    }

    val wikipedia = wikipediaSearchSettings.enabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun setWikipedia(wikipedia: Boolean) {
        wikipediaSearchSettings.setEnabled(wikipedia)
    }

    val websites = websiteSearchSettings.enabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun setWebsites(websites: Boolean) {
        websiteSearchSettings.setEnabled(websites)
    }

    val autoFocus = searchUiSettings.openKeyboard

    fun setAutoFocus(autoFocus: Boolean) {
        searchUiSettings.setOpenKeyboard(autoFocus)
    }

    val launchOnEnter = searchUiSettings.launchOnEnter
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun setLaunchOnEnter(launchOnEnter: Boolean) {
        searchUiSettings.setLaunchOnEnter(launchOnEnter)
    }

    val hasAppShortcutPermission = permissionsManager.hasPermission(PermissionGroup.AppShortcuts)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
    val appShortcuts = shortcutSearchSettings.enabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun setAppShortcuts(appShortcuts: Boolean) {
        shortcutSearchSettings.setEnabled(appShortcuts)
    }

    val searchResultOrdering = searchUiSettings.resultOrder
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun setSearchResultOrdering(searchResultOrdering: SearchResultOrder) {
        searchUiSettings.setResultOrder(searchResultOrdering)
    }


    val reverseSearchResults = searchUiSettings.reversedResults
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun setReverseSearchResults(reverseSearchResults: Boolean) {
        searchUiSettings.setReversedResults(reverseSearchResults)
    }

    fun requestAppShortcutsPermission(activity: AppCompatActivity) {
        permissionsManager.requestPermission(activity, PermissionGroup.AppShortcuts)
    }

    val filterBar = searchFilterSettings.filterBar
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun setFilterBar(filterBar: Boolean) {
        searchFilterSettings.setFilterBar(filterBar)
    }

    val searchFilters = searchFilterSettings.defaultFilter
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), SearchFilters())

    fun setSearchFilters(searchFilters: SearchFilters) {
        searchFilterSettings.setDefaultFilter(searchFilters)
    }
}