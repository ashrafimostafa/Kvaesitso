package ir.mostafa.launcher.searchactions.builders

import android.content.Context
import ir.mostafa.launcher.searchactions.R
import ir.mostafa.launcher.searchactions.TextClassificationResult
import ir.mostafa.launcher.searchactions.actions.SearchAction
import ir.mostafa.launcher.searchactions.actions.SearchActionIcon
import ir.mostafa.launcher.searchactions.actions.TimerAction

class TimerActionBuilder(
    override val label: String,
) : SearchActionBuilder {
    constructor(context: Context) : this(context.getString(R.string.search_action_timer))

    override val key: String
        get() = "timer"

    override val icon: SearchActionIcon = SearchActionIcon.Timer

    override fun build(context: Context, classifiedQuery: TextClassificationResult): SearchAction? {
        // Maximum time span for a timer is 24 hours, see https://developer.android.com/reference/android/provider/AlarmClock#EXTRA_LENGTH
        if (classifiedQuery.timespan != null && classifiedQuery.timespan.seconds <= 86400) {
            return TimerAction(
                context.getString(R.string.search_action_timer), classifiedQuery.timespan
            )
        }
        return null
    }

}