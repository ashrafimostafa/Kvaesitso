package ir.mostafa.launcher.search

import kotlinx.coroutines.flow.Flow

interface SearchableRepository<T : Searchable> {
    fun search(query: String, allowNetwork: Boolean): Flow<List<T>>
}