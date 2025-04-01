package ir.mostafa.launcher.searchactions.builders

import android.content.Context
import ir.mostafa.launcher.searchactions.R
import ir.mostafa.launcher.searchactions.TextClassificationResult
import ir.mostafa.launcher.searchactions.actions.SearchAction
import ir.mostafa.launcher.searchactions.actions.SearchActionIcon
import ir.mostafa.launcher.searchactions.actions.SetAlarmAction

class SetAlarmActionBuilder(
    override val label: String
) : SearchActionBuilder {

    constructor(context: Context) : this(context.getString(R.string.search_action_alarm))

    override val key: String
        get() = "alarm"

    override val icon: SearchActionIcon = SearchActionIcon.Alarm

    override fun build(context: Context, classifiedQuery: TextClassificationResult): SearchAction? {
        if (classifiedQuery.time != null) {
            return SetAlarmAction(
                context.getString(R.string.search_action_alarm), classifiedQuery.time
            )
        }
        return null
    }

}