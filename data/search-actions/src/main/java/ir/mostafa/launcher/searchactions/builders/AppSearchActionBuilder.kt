package ir.mostafa.launcher.searchactions.builders

import android.content.Context
import android.content.Intent
import ir.mostafa.launcher.searchactions.TextClassificationResult
import ir.mostafa.launcher.searchactions.actions.AppSearchAction
import ir.mostafa.launcher.searchactions.actions.SearchAction
import ir.mostafa.launcher.searchactions.actions.SearchActionIcon

data class AppSearchActionBuilder(
    override val label: String,
    val baseIntent: Intent,
    override val icon: SearchActionIcon = SearchActionIcon.Custom,
    override val iconColor: Int = 0,
    override val customIcon: String? = null,
) : CustomizableSearchActionBuilder {

    override val key: String
        get() = "app://${baseIntent.toUri(0)}"
    override fun build(context: Context, classifiedQuery: TextClassificationResult): SearchAction {
        return AppSearchAction(
            label = label,
            baseIntent = baseIntent,
            query = classifiedQuery.text,
            icon = icon,
            iconColor = iconColor,
            customIcon = customIcon,
        )
    }
}