package de.mm20.launcher2.ui.launcher.sheets

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import de.mm20.launcher2.ui.launcher.PagerScaffold
import de.mm20.launcher2.ui.theme.LauncherTheme




@Composable
fun LauncherBottomSheets() {
    val bottomSheetManager = LocalBottomSheetManager.current
    bottomSheetManager.customizeSearchableSheetShown.value?.let {
        CustomizeSearchableSheet(
            searchable = it,
            onDismiss = { bottomSheetManager.dismissCustomizeSearchableModal() })
    }
    if (bottomSheetManager.editFavoritesSheetShown.value) {
        EditFavoritesSheet(onDismiss = { bottomSheetManager.dismissEditFavoritesSheet() })
    }
}