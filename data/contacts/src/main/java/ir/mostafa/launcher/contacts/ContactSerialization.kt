package ir.mostafa.launcher.contacts

import ir.mostafa.launcher.ktx.jsonObjectOf
import ir.mostafa.launcher.permissions.PermissionGroup
import ir.mostafa.launcher.permissions.PermissionsManager
import ir.mostafa.launcher.search.SavableSearchable
import ir.mostafa.launcher.search.SearchableDeserializer
import ir.mostafa.launcher.search.SearchableSerializer
import kotlinx.coroutines.flow.first
import org.json.JSONObject

internal class ContactSerializer : SearchableSerializer {
    override fun serialize(searchable: SavableSearchable): String {
        searchable as AndroidContact
        return jsonObjectOf(
            "id" to searchable.id
        ).toString()
    }

    override val typePrefix: String
        get() = "contact"
}

internal class ContactDeserializer(
    private val contactRepository: ContactRepository,
    private val permissionsManager: PermissionsManager
) : SearchableDeserializer {

    override suspend fun deserialize(serialized: String): SavableSearchable? {
        if (!permissionsManager.checkPermissionOnce(PermissionGroup.Contacts)) return null
        val id = JSONObject(serialized).getLong("id")

        return contactRepository.get(id).first()
    }
}