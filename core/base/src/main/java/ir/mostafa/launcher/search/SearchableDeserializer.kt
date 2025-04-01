package ir.mostafa.launcher.search

interface SearchableDeserializer {
    suspend fun deserialize(serialized: String): SavableSearchable?
}

class NullDeserializer: SearchableDeserializer {
    override suspend fun deserialize(serialized: String): SavableSearchable? {
        return null
    }

}