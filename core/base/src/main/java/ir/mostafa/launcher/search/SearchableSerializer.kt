package ir.mostafa.launcher.search

interface SearchableSerializer {
    fun serialize(searchable: SavableSearchable): String?
    val typePrefix: String
}

class NullSerializer : SearchableSerializer{
    override fun serialize(searchable: SavableSearchable): String? {
        return null
    }

    override val typePrefix: String
        get() = "null"

}