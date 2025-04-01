package ir.mostafa.launcher.ui.launcher.search.website

import androidx.compose.foundation.lazy.LazyListScope
import ir.mostafa.launcher.search.Website
import ir.mostafa.launcher.ui.launcher.search.common.list.ListItem
import ir.mostafa.launcher.ui.launcher.search.common.list.ListResults

fun LazyListScope.WebsiteResults(
    websites: List<Website>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    highlightedItem: Website?,
    reverse: Boolean,
) {
    ListResults(
        key = "website",
        items = websites,
        itemContent = { website, showDetails, index ->
            ListItem(
                item = website,
                showDetails = showDetails,
                onShowDetails = { onSelect(if(it) index else -1) },
                highlight = website.key == highlightedItem?.key,
            )
        },
        selectedIndex = selectedIndex,
        reverse = reverse,
    )
}