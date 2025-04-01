package ir.mostafa.launcher.websites

import ir.mostafa.launcher.ktx.jsonObjectOf
import ir.mostafa.launcher.search.SavableSearchable
import ir.mostafa.launcher.search.SearchableDeserializer
import ir.mostafa.launcher.search.SearchableSerializer
import org.json.JSONObject

class WebsiteSerializer : SearchableSerializer {
    override fun serialize(searchable: SavableSearchable): String {
        searchable as WebsiteImpl
        return jsonObjectOf(
            "label" to searchable.label,
            "url" to searchable.url,
            "description" to searchable.description,
            "image" to searchable.imageUrl,
            "favicon" to searchable.faviconUrl,
            "color" to searchable.color
        ).toString()
    }

    override val typePrefix: String
        get() = "website"
}

class WebsiteDeserializer: SearchableDeserializer {
    override suspend fun deserialize(serialized: String): SavableSearchable? {
        val json = JSONObject(serialized)
        return WebsiteImpl(
            label = json.getString("label"),
            faviconUrl = json.optString("favicon").takeIf { it.isNotBlank() },
            imageUrl = json.optString("image").takeIf { it.isNotBlank() },
            description = json.optString("description").takeIf { it.isNotBlank() },
            url = json.getString("url"),
            color = json.optInt("color").takeIf { it != 0 }
        )
    }
}