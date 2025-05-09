package ir.mostafa.launcher.contacts

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import androidx.core.graphics.drawable.toDrawable
import ir.mostafa.launcher.icons.ColorLayer
import ir.mostafa.launcher.icons.LauncherIcon
import ir.mostafa.launcher.icons.StaticIconLayer
import ir.mostafa.launcher.icons.StaticLauncherIcon
import ir.mostafa.launcher.icons.TextLayer
import ir.mostafa.launcher.ktx.asBitmap
import ir.mostafa.launcher.ktx.tryStartActivity
import ir.mostafa.launcher.search.Contact
import ir.mostafa.launcher.search.ContactApp
import ir.mostafa.launcher.search.EmailAddress
import ir.mostafa.launcher.search.PhoneNumber
import ir.mostafa.launcher.search.PostalAddress
import ir.mostafa.launcher.search.SearchableSerializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal data class AndroidContact(
    internal val id: Long,
    override val firstName: String,
    override val lastName: String,
    override val displayName: String,
    override val phoneNumbers: List<PhoneNumber>,
    override val emailAddresses: List<EmailAddress>,
    override val postalAddresses: List<PostalAddress>,
    override val contactApps: List<ContactApp>,
    internal val lookupKey: String,
    override val labelOverride: String? = null,
) : Contact {


    override val domain: String = Domain
    override val key: String
        get() = "$Domain://$id"
    override val label: String
        get() = displayName.takeIf { it.isNotBlank() } ?: "$firstName $lastName"

    override val summary: String
        get() {
            return (phoneNumbers.map { it.number } + emailAddresses.map { it.address }).joinToString(", ")
        }

    override fun overrideLabel(label: String): Contact {
        return copy(labelOverride = label)
    }

    override fun launch(context: Context, options: Bundle?): Boolean {
        val uri =
            ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id)
        val intent = Intent(Intent.ACTION_VIEW).setData(uri).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return context.tryStartActivity(intent, options)
    }

    override fun getPlaceholderIcon(context: Context): StaticLauncherIcon {
        val iconText =
            if (firstName.isNotEmpty()) firstName[0].toString() else "" + if (lastName.isNotEmpty()) lastName[0].toString() else ""

        return StaticLauncherIcon(
            foregroundLayer = TextLayer(text = iconText, color = 0xFF2364AA.toInt()),
            backgroundLayer = ColorLayer(0xFF2364AA.toInt())
        )
    }

    override suspend fun loadIcon(
        context: Context,
        size: Int,
        themed: Boolean,
    ): LauncherIcon? {
        val contentResolver = context.contentResolver
        val bmp = withContext(Dispatchers.IO) {
            val uri =
                ContactsContract.Contacts.getLookupUri(id, lookupKey) ?: return@withContext null
            ContactsContract.Contacts.openContactPhotoInputStream(contentResolver, uri, false)
                ?.asBitmap()
        } ?: return null

        return StaticLauncherIcon(
            foregroundLayer = StaticIconLayer(
                icon = bmp.toDrawable(context.resources),
            ),
            backgroundLayer = ColorLayer(0xFF2364AA.toInt())
        )
    }

    override fun getSerializer(): SearchableSerializer {
        return ContactSerializer()
    }

    companion object {
        const val Domain = "contact"
    }
}