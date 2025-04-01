package ir.mostafa.launcher.searchactions.builders

import android.content.Context
import ir.mostafa.launcher.searchactions.R
import ir.mostafa.launcher.searchactions.TextClassificationResult
import ir.mostafa.launcher.searchactions.actions.MessageAction
import ir.mostafa.launcher.searchactions.actions.SearchAction
import ir.mostafa.launcher.searchactions.actions.SearchActionIcon

class MessageActionBuilder(
    override val label: String
): SearchActionBuilder {

    constructor(context: Context) : this(context.getString(R.string.search_action_message))

    override val key: String
        get() = "message"

    override val icon: SearchActionIcon = SearchActionIcon.Message

    override fun build(context: Context, classifiedQuery: TextClassificationResult): SearchAction? {
        if (classifiedQuery.phoneNumber != null) {
            return MessageAction(
                context.getString(R.string.search_action_message), classifiedQuery.phoneNumber
            )
        }
        return null
    }
}