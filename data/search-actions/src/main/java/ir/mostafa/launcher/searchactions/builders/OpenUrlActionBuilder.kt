package ir.mostafa.launcher.searchactions.builders

import android.content.Context
import ir.mostafa.launcher.searchactions.R
import ir.mostafa.launcher.searchactions.TextClassificationResult
import ir.mostafa.launcher.searchactions.actions.OpenUrlAction
import ir.mostafa.launcher.searchactions.actions.SearchAction
import ir.mostafa.launcher.searchactions.actions.SearchActionIcon

class OpenUrlActionBuilder(
    override val label: String
) : SearchActionBuilder {

    constructor(context: Context) : this(context.getString(R.string.search_action_open_url))

    override val key: String
        get() = "website"

    override val icon: SearchActionIcon = SearchActionIcon.Website

    override fun build(context: Context, classifiedQuery: TextClassificationResult): SearchAction? {
        if (classifiedQuery.url != null) {
            return OpenUrlAction(
                context.getString(R.string.search_action_open_url), classifiedQuery.url
            )
        }
        return null
    }
}