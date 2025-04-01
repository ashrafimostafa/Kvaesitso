package ir.mostafa.launcher.services.tags

import ir.mostafa.launcher.search.SavableSearchable
import kotlinx.coroutines.flow.Flow

interface TagsService {
    fun getAllTags(startsWith: String? = null): Flow<List<String>>
    fun deleteTag(tag: String)
    fun cloneTag(tag: String, newTag: String)
    fun getTaggedItems(tag: String): Flow<List<SavableSearchable>>

    fun createTag(tag: String, items: List<SavableSearchable>)

    fun updateTag(tag: String, newName: String? = null, items: List<SavableSearchable>? = null)
    fun getTags(it: SavableSearchable): Flow<List<String>>
}