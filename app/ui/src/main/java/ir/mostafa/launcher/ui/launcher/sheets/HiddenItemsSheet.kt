package ir.mostafa.launcher.ui.launcher.sheets

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.mostafa.launcher.search.SavableSearchable
import ir.mostafa.launcher.ui.R
import ir.mostafa.launcher.ui.component.BottomSheetDialog
import ir.mostafa.launcher.ui.launcher.search.common.grid.SearchResultGrid

@Composable
fun HiddenItemsSheet(
    items: List<SavableSearchable>,
    onDismiss: () -> Unit
) {
    val viewModel: HiddenItemsSheetVM = viewModel()

    val context = LocalContext.current

    BottomSheetDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                stringResource(R.string.preference_hidden_items),
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(end = 16.dp),
                maxLines = 1
            )
        },
        actions = {
            IconButton(onClick = { viewModel.showHiddenItems(context) }) {
                Icon(imageVector = Icons.Rounded.Edit, contentDescription = null)
            }
        },
    ) {

        SearchResultGrid(
            items,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(it)
        )
    }
}
