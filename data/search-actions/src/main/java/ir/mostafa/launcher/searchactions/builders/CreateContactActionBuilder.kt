package ir.mostafa.launcher.searchactions.builders

import android.content.Context
import ir.mostafa.launcher.searchactions.R
import ir.mostafa.launcher.searchactions.TextClassificationResult
import ir.mostafa.launcher.searchactions.actions.CreateContactAction
import ir.mostafa.launcher.searchactions.actions.SearchAction
import ir.mostafa.launcher.searchactions.actions.SearchActionIcon

class CreateContactActionBuilder(
    override val label: String
) : SearchActionBuilder {

    constructor(context: Context) : this(context.getString(R.string.search_action_contact))

    override val key: String
        get() = "contact"

    override val icon: SearchActionIcon = SearchActionIcon.Contact

    override fun build(context: Context, classifiedQuery: TextClassificationResult): SearchAction? {
        if (classifiedQuery.phoneNumber != null || classifiedQuery.email != null) {
            return CreateContactAction(
                context.getString(R.string.search_action_contact),
                phone = classifiedQuery.phoneNumber,
                email = classifiedQuery.email,
            )
        }
        return null
    }

}