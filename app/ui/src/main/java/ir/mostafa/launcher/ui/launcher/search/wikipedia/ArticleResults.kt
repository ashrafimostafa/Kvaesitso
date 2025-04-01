package ir.mostafa.launcher.ui.launcher.search.wikipedia

import androidx.compose.foundation.lazy.LazyListScope
import ir.mostafa.launcher.search.Article
import ir.mostafa.launcher.ui.launcher.search.common.list.ListItem
import ir.mostafa.launcher.ui.launcher.search.common.list.ListResults

fun LazyListScope.ArticleResults(
    articles: List<Article>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    highlightedItem: Article?,
    reverse: Boolean,
) {
    ListResults(
        key = "article",
        items = articles,
        itemContent = { article, showDetails, index ->
            ListItem(
                item = article,
                showDetails = showDetails,
                onShowDetails = { onSelect(if(it) index else -1) },
                highlight = article.key == highlightedItem?.key,
            )
        },
        selectedIndex = selectedIndex,
        reverse = reverse,
    )
}