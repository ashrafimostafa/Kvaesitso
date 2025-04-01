package ir.mostafa.launcher.searchactions.builders

import android.content.Context
import ir.mostafa.launcher.searchactions.R
import ir.mostafa.launcher.searchactions.actions.SearchAction
import ir.mostafa.launcher.searchactions.TextClassificationResult
import ir.mostafa.launcher.searchactions.actions.CallAction
import ir.mostafa.launcher.searchactions.actions.SearchActionIcon

class CallActionBuilder(
    override val label: String
): SearchActionBuilder {

    constructor(context: Context): this(context.getString(R.string.search_action_call))

    override val key: String
        get() = "call"

    override val icon: SearchActionIcon = SearchActionIcon.Phone

    override fun build(context: Context, classifiedQuery: TextClassificationResult): SearchAction? {
        if (classifiedQuery.phoneNumber != null) {
            return CallAction(
                context.getString(R.string.search_action_call), classifiedQuery.phoneNumber
            )
        }
        return null
    }

}