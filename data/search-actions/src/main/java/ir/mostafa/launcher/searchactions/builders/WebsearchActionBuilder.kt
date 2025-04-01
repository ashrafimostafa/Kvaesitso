package ir.mostafa.launcher.searchactions.builders

import android.content.Context
import ir.mostafa.launcher.searchactions.R
import ir.mostafa.launcher.searchactions.TextClassificationResult
import ir.mostafa.launcher.searchactions.actions.SearchAction
import ir.mostafa.launcher.searchactions.actions.SearchActionIcon
import ir.mostafa.launcher.searchactions.actions.WebsearchAction

class WebsearchActionBuilder(
    override val label: String,
) : SearchActionBuilder {

    constructor(context: Context) : this(context.getString(R.string.search_action_websearch))

    override val icon: SearchActionIcon = SearchActionIcon.WebSearch
    override val key: String
        get() = "websearch"

    override fun build(context: Context, classifiedQuery: TextClassificationResult): SearchAction {
        return WebsearchAction(
            context.getString(R.string.search_action_websearch), classifiedQuery.text
        )
    }
}