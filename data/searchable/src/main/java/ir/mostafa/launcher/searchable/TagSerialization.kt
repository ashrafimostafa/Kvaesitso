package ir.mostafa.launcher.searchable

import ir.mostafa.launcher.search.SavableSearchable
import ir.mostafa.launcher.search.SearchableDeserializer
import ir.mostafa.launcher.search.SearchableSerializer
import ir.mostafa.launcher.search.data.Tag
import org.json.JSONObject

class TagSerializer: SearchableSerializer {
    override fun serialize(searchable: SavableSearchable): String {
        searchable as Tag
        val json = JSONObject()
        json.put("tag", searchable.tag)
        return json.toString()
    }

    override val typePrefix: String
        get() = "tag"
}

class TagDeserializer: SearchableDeserializer {
    override suspend fun deserialize(serialized: String): SavableSearchable {
        val json = JSONObject(serialized)

        return Tag(json.getString("tag"))
    }

}