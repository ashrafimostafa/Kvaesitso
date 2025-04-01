package ir.mostafa.launcher.ui.launcher.search.location

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ir.mostafa.launcher.search.Location
import ir.mostafa.launcher.ui.R
import ir.mostafa.launcher.ui.component.MissingPermissionBanner
import ir.mostafa.launcher.ui.launcher.search.common.ShowAllButton
import ir.mostafa.launcher.ui.launcher.search.common.list.ListItem
import ir.mostafa.launcher.ui.launcher.search.common.list.ListResults
import kotlin.math.min

fun LazyListScope.LocationResults(
    locations: List<Location>,
    missingPermission: Boolean,
    onPermissionRequest: () -> Unit,
    onPermissionRequestRejected: () -> Unit,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    highlightedItem: Location?,
    reverse: Boolean,
    truncate: Boolean,
    onShowAll: () -> Unit,
) {
    ListResults(
        items = locations.subList(0, if (truncate) min(5, locations.size) else locations.size),
        key = "location",
        reverse = reverse,
        selectedIndex = selectedIndex,
        itemContent = { location, showDetails, index ->
            ListItem(
                modifier = Modifier
                    .fillMaxWidth(),
                item = location,
                showDetails = showDetails,
                onShowDetails = { onSelect(if(it) index else -1) },
                highlight = highlightedItem?.key == location.key
            )
        },
        before = if (missingPermission) {
            {
                MissingPermissionBanner(
                    modifier = Modifier.padding(8.dp),
                    text = stringResource(R.string.missing_permission_location_search),
                    onClick = onPermissionRequest,
                    secondaryAction = {
                        OutlinedButton(onClick = onPermissionRequestRejected) {
                            Text(
                                stringResource(R.string.turn_off),
                            )
                        }
                    }
                )
            }
        } else null,
        after = if (truncate && locations.size > 5) {
            {
                ShowAllButton(onShowAll = onShowAll)
            }
        } else null
    )
}