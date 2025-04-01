package ir.mostafa.launcher.searchactions.actions

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import ir.mostafa.launcher.ktx.tryStartActivity

data class AppSearchAction(
    override val label: String,
    val baseIntent: Intent,
    val query: String,
    override val icon: SearchActionIcon = SearchActionIcon.Custom,
    override val iconColor: Int = 1,
    override val customIcon: String? = null,
): SearchAction {

    override fun start(context: Context) {
        val intent = Intent(baseIntent).apply {
            action = Intent.ACTION_SEARCH
            putExtra(SearchManager.QUERY, query)
            putExtra(SearchManager.USER_QUERY, query)
        }
        context.tryStartActivity(intent)
    }
}