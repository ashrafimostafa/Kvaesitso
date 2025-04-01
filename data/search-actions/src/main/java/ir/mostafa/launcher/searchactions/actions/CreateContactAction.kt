package ir.mostafa.launcher.searchactions.actions

import android.content.Context
import android.content.Intent
import android.provider.ContactsContract
import android.provider.ContactsContract.Intents.Insert
import ir.mostafa.launcher.ktx.tryStartActivity

class CreateContactAction(
    override val label: String,
    val phone: String? = null,
    val email: String? = null,
) : SearchAction {
    override val icon: SearchActionIcon = SearchActionIcon.Contact
    override val iconColor: Int = 0
    override val customIcon: String? = null

    override fun start(context: Context) {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            type = ContactsContract.Contacts.CONTENT_TYPE
            if (email != null) putExtra(Insert.EMAIL, email)
            if (phone != null) putExtra(Insert.PHONE, phone)
        }
        context.tryStartActivity(intent)
    }
}