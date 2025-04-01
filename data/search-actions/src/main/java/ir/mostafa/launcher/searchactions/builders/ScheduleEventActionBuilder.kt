package ir.mostafa.launcher.searchactions.builders

import android.content.Context
import ir.mostafa.launcher.searchactions.R
import ir.mostafa.launcher.searchactions.TextClassificationResult
import ir.mostafa.launcher.searchactions.actions.ScheduleEventAction
import ir.mostafa.launcher.searchactions.actions.SearchAction
import ir.mostafa.launcher.searchactions.actions.SearchActionIcon
import java.time.LocalDateTime

class ScheduleEventActionBuilder(
    override val label: String
) : SearchActionBuilder {

    constructor(context: Context) : this(context.getString(R.string.search_action_event))

    override val key: String
        get() = "calendar"

    override val icon: SearchActionIcon = SearchActionIcon.Calendar

    override fun build(context: Context, classifiedQuery: TextClassificationResult): SearchAction? {
        if (classifiedQuery.date != null) {
            return ScheduleEventAction(
                context.getString(R.string.search_action_event),
                date = classifiedQuery.date,
                time = classifiedQuery.time
            )
        }
        // Time spans that are shorter than 24 hours are already handled by the TimerActionBuilder
        if (classifiedQuery.timespan != null && classifiedQuery.timespan.seconds > 86400
            && classifiedQuery.timespan.seconds < 3153600000
        ) {
            val datetime = LocalDateTime.now().plus(classifiedQuery.timespan)
            return ScheduleEventAction(
                context.getString(R.string.search_action_event),
                date = datetime.toLocalDate(),
                time = datetime.toLocalTime(),
            )
        }
        return null
    }
}