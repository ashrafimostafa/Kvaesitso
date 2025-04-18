package ir.mostafa.launcher.ui.launcher.search

import androidx.activity.compose.BackHandler
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.mostafa.launcher.profiles.Profile
import ir.mostafa.launcher.search.AppShortcut
import ir.mostafa.launcher.search.Application
import ir.mostafa.launcher.search.Article
import ir.mostafa.launcher.search.CalendarEvent
import ir.mostafa.launcher.search.Contact
import ir.mostafa.launcher.search.File
import ir.mostafa.launcher.search.Location
import ir.mostafa.launcher.search.Website
import ir.mostafa.launcher.ui.component.LauncherCard
import ir.mostafa.launcher.ui.launcher.search.apps.AppResults
import ir.mostafa.launcher.ui.launcher.search.calculator.CalculatorResults
import ir.mostafa.launcher.ui.launcher.search.calendar.CalendarResults
import ir.mostafa.launcher.ui.launcher.search.contacts.ContactResults
import ir.mostafa.launcher.ui.launcher.search.favorites.SearchFavorites
import ir.mostafa.launcher.ui.launcher.search.favorites.SearchFavoritesVM
import ir.mostafa.launcher.ui.launcher.search.files.FileResults
import ir.mostafa.launcher.ui.launcher.search.filters.SearchFilters
import ir.mostafa.launcher.ui.launcher.search.location.LocationResults
import ir.mostafa.launcher.ui.launcher.search.shortcut.ShortcutResults
import ir.mostafa.launcher.ui.launcher.search.unitconverter.UnitConverterResults
import ir.mostafa.launcher.ui.launcher.search.website.WebsiteResults
import ir.mostafa.launcher.ui.launcher.search.wikipedia.ArticleResults
import ir.mostafa.launcher.ui.launcher.sheets.HiddenItemsSheet
import ir.mostafa.launcher.ui.launcher.sheets.LocalBottomSheetManager
import ir.mostafa.launcher.ui.locals.LocalCardStyle
import ir.mostafa.launcher.ui.locals.LocalGridSettings

@Composable
fun SearchColumn(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    state: LazyListState = rememberLazyListState(),
    reverse: Boolean = false,
    userScrollEnabled: Boolean = true,
) {

    val columns = LocalGridSettings.current.columnCount
    val context = LocalContext.current

    val viewModel: SearchVM = viewModel()

    val favoritesVM: SearchFavoritesVM = viewModel()
    val favorites by favoritesVM.favorites.collectAsState(emptyList())

    val hideFavs by viewModel.hideFavorites
    val favoritesEnabled by viewModel.favoritesEnabled.collectAsState(false)

    val apps by viewModel.appResults
    val workApps by viewModel.workAppResults
    val privateApps by viewModel.privateSpaceAppResults
    val profiles by viewModel.profiles.collectAsState(emptyList())
    val profileStates by viewModel.profileStates.collectAsState(emptyList())

    val appShortcuts by viewModel.appShortcutResults
    val contacts by viewModel.contactResults
    val files by viewModel.fileResults
    val events by viewModel.calendarResults
    val unitConverter by viewModel.unitConverterResults
    val calculator by viewModel.calculatorResults
    val wikipedia by viewModel.articleResults
    val locations by viewModel.locationResults
    val website by viewModel.websiteResults
    val hiddenResults by viewModel.hiddenResults

    val bestMatch by viewModel.bestMatch

    val isSearchEmpty by viewModel.isSearchEmpty

    val missingCalendarPermission by viewModel.missingCalendarPermission.collectAsState(false)
    val missingShortcutsPermission by viewModel.missingAppShortcutPermission.collectAsState(false)
    val missingContactsPermission by viewModel.missingContactsPermission.collectAsState(false)
    val missingLocationPermission by viewModel.missingLocationPermission.collectAsState(false)
    val missingFilesPermission by viewModel.missingFilesPermission.collectAsState(false)
    val hasProfilesPermission by viewModel.hasProfilesPermission.collectAsState(false)

    val pinnedTags by favoritesVM.pinnedTags.collectAsState(emptyList())
    val selectedTag by favoritesVM.selectedTag.collectAsState(null)
    val favoritesEditButton by favoritesVM.showEditButton.collectAsState(false)
    val favoritesTagsExpanded by favoritesVM.tagsExpanded.collectAsState(false)

    val expandedCategory: SearchCategory? by viewModel.expandedCategory

    var selectedAppProfileIndex: Int by remember(isSearchEmpty) { mutableIntStateOf(0) }
    var selectedContactIndex: Int by remember(contacts) { mutableIntStateOf(-1) }
    var selectedFileIndex: Int by remember(files) { mutableIntStateOf(-1) }
    var selectedCalendarIndex: Int by remember(events) { mutableIntStateOf(-1) }
    var selectedLocationIndex: Int by remember(locations) { mutableIntStateOf(-1) }
    var selectedShortcutIndex: Int by remember(appShortcuts) { mutableIntStateOf(-1) }
    var selectedArticleIndex: Int by remember(wikipedia) { mutableIntStateOf(-1) }
    var selectedWebsiteIndex: Int by remember(website) { mutableIntStateOf(-1) }

    val showFilters by viewModel.showFilters

    AnimatedContent(
        showFilters,
        modifier = modifier.padding(horizontal = 8.dp),
    ) {
        if (it) {
            BackHandler {
                viewModel.showFilters.value = false
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = if (reverse) Alignment.BottomCenter else Alignment.TopCenter,
            ) {
                SearchFilters(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
                            MaterialTheme.shapes.medium
                        )
                        .padding(12.dp),
                    filters = viewModel.filters.value,
                    onFiltersChange = {
                        viewModel.setFilters(it)
                    }
                )
            }
        } else {
            LazyColumn(
                state = state,
                userScrollEnabled = userScrollEnabled,
                contentPadding = paddingValues,
                reverseLayout = reverse,
            ) {
                if (!hideFavs && favoritesEnabled) {
                    SearchFavorites(
                        favorites = favorites,
                        selectedTag = selectedTag,
                        pinnedTags = pinnedTags,
                        tagsExpanded = favoritesTagsExpanded,
                        onSelectTag = { favoritesVM.selectTag(it) },
                        reverse = reverse,
                        onExpandTags = {
                            favoritesVM.setTagsExpanded(it)
                        },
                        editButton = favoritesEditButton
                    )
                }

                if (isSearchEmpty && profiles.size > 1) {
                    AppResults(
                        apps = when(profiles.getOrNull(selectedAppProfileIndex)?.type) {
                            Profile.Type.Private -> privateApps
                            Profile.Type.Work -> workApps
                            else -> apps
                        },
                        highlightedItem = bestMatch as? Application,
                        profiles = profiles,
                        selectedProfileIndex = selectedAppProfileIndex,
                        onProfileSelected = {
                            selectedAppProfileIndex = it
                        },
                        isProfileLocked = profileStates.getOrNull(selectedAppProfileIndex)?.locked == true,
                        onProfileLockChange = { p, l ->
                            viewModel.setProfileLock(p, l)
                        },
                        columns = columns,
                        reverse = reverse,
                        showProfileLockControls = hasProfilesPermission,
                    )
                } else {
                    AppResults(
                        apps = apps,
                        highlightedItem = bestMatch as? Application,
                        onProfileSelected = {
                            selectedAppProfileIndex = it
                        },
                        columns = columns,
                        reverse = reverse
                    )
                }

                if (!isSearchEmpty) {

                    ShortcutResults(
                        shortcuts = appShortcuts,
                        missingPermission = missingShortcutsPermission,
                        onPermissionRequest = {
                            viewModel.requestAppShortcutPermission(context as AppCompatActivity)
                        },
                        onPermissionRequestRejected = {
                            viewModel.disableAppShortcutSearch()
                        },
                        reverse = reverse,
                        selectedIndex = selectedShortcutIndex,
                        onSelect = { selectedShortcutIndex = it },
                        highlightedItem = bestMatch as? AppShortcut,
                        truncate = expandedCategory != SearchCategory.Shortcuts,
                        onShowAll = {
                            viewModel.expandCategory(SearchCategory.Shortcuts)
                        },
                    )

                    UnitConverterResults(
                        converters = unitConverter,
                        reverse = reverse,
                        truncate = expandedCategory != SearchCategory.UnitConverter,
                        onShowAll = {
                            viewModel.expandCategory(SearchCategory.UnitConverter)
                        }
                    )

                    CalculatorResults(
                        calculator,
                        reverse = reverse
                    )

                    CalendarResults(
                        events = events,
                        missingPermission = missingCalendarPermission,
                        onPermissionRequest = {
                            viewModel.requestCalendarPermission(context as AppCompatActivity)
                        },
                        onPermissionRequestRejected = {
                            viewModel.disableCalendarSearch()
                        },
                        reverse = reverse,
                        selectedIndex = selectedCalendarIndex,
                        onSelect = { selectedCalendarIndex = it },
                        highlightedItem = bestMatch as? CalendarEvent,
                        truncate = expandedCategory != SearchCategory.Calendar,
                        onShowAll = {
                            viewModel.expandCategory(SearchCategory.Calendar)
                        }
                    )

                    ContactResults(
                        contacts = contacts,
                        missingPermission = missingContactsPermission,
                        onPermissionRequest = {
                            viewModel.requestContactsPermission(context as AppCompatActivity)
                        },
                        onPermissionRequestRejected = {
                            viewModel.disableContactsSearch()
                        },
                        reverse = reverse,
                        selectedIndex = selectedContactIndex,
                        onSelect = { selectedContactIndex = it },
                        highlightedItem = bestMatch as? Contact,
                        truncate = expandedCategory != SearchCategory.Contacts,
                        onShowAll = {
                            viewModel.expandCategory(SearchCategory.Contacts)
                        },
                    )

                    LocationResults(
                        locations = locations,
                        missingPermission = missingLocationPermission,
                        onPermissionRequest = {
                            viewModel.requestLocationPermission(context as AppCompatActivity)
                        },
                        onPermissionRequestRejected = {
                            viewModel.disableLocationSearch()
                        },
                        reverse = reverse,
                        selectedIndex = selectedLocationIndex,
                        onSelect = { selectedLocationIndex = it },
                        highlightedItem = bestMatch as? Location,
                        truncate = expandedCategory != SearchCategory.Location,
                        onShowAll = {
                            viewModel.expandCategory(SearchCategory.Location)
                        }
                    )
                    ArticleResults(
                        articles = wikipedia,
                        selectedIndex = selectedArticleIndex,
                        onSelect = { selectedArticleIndex = it },
                        highlightedItem = bestMatch as? Article,
                        reverse = reverse,
                    )
                    WebsiteResults(
                        websites = website,
                        selectedIndex = selectedWebsiteIndex,
                        onSelect = { selectedWebsiteIndex = it },
                        highlightedItem = bestMatch as? Website,
                        reverse = reverse,
                    )
                    FileResults(
                        files = files,
                        onPermissionRequest = {
                            viewModel.requestFilesPermission(context as AppCompatActivity)
                        },
                        onPermissionRequestRejected = {
                            viewModel.disableFilesSearch()
                        },
                        reverse = reverse,
                        highlightedItem = bestMatch as? File,
                        missingPermission = missingFilesPermission,
                        selectedIndex = selectedFileIndex,
                        onSelect = {
                            selectedFileIndex = it
                        },
                        truncate = expandedCategory != SearchCategory.Files,
                        onShowAll = {
                            viewModel.expandCategory(SearchCategory.Files)
                        }
                    )
                }
            }
        }

    }


    val sheetManager = LocalBottomSheetManager.current
    if (sheetManager.hiddenItemsSheetShown.value) {
        HiddenItemsSheet(
            items = hiddenResults,
            onDismiss = { sheetManager.dismissHiddenItemsSheet() })
    }
}


fun LazyListScope.SingleResult(
    highlight: Boolean = false,
    content: @Composable (() -> Unit)?
) {
    if (content == null) return
    item {
        LauncherCard(
            modifier = Modifier
                .padding(
                    horizontal = 8.dp,
                    vertical = 4.dp,
                ),
            color = if (highlight) MaterialTheme.colorScheme.secondaryContainer
            else MaterialTheme.colorScheme.surface.copy(LocalCardStyle.current.opacity)
        ) {
            content()
        }
    }
}

