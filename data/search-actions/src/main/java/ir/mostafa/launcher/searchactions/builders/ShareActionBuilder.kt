package ir.mostafa.launcher.searchactions.builders

import android.content.Context
import ir.mostafa.launcher.searchactions.R
import ir.mostafa.launcher.searchactions.TextClassificationResult
import ir.mostafa.launcher.searchactions.actions.SearchAction
import ir.mostafa.launcher.searchactions.actions.SearchActionIcon
import ir.mostafa.launcher.searchactions.actions.ShareAction

class ShareActionBuilder(
    override val label: String,
) : SearchActionBuilder {

    constructor(context: Context) : this(context.getString(R.string.search_action_share))

    override val key: String
        get() = "share"

    override val icon: SearchActionIcon = SearchActionIcon.Share

    override fun build(context: Context, classifiedQuery: TextClassificationResult): SearchAction? {
        return ShareAction(
            context.getString(R.string.search_action_share),
            classifiedQuery.text
        )
    }
}