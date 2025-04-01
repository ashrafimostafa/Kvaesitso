package ir.mostafa.launcher.applications

import android.content.Context
import ir.mostafa.launcher.search.Application
import ir.mostafa.launcher.search.SearchableRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * App repository that returns a fixed number of fake apps to simulate a large number of apps.
 */
class FakeAppRepository(private val context: Context, private val fakePackages: Int) : SearchableRepository<Application> {


    override fun search(query: String, allowNetwork: Boolean): Flow<ImmutableList<Application>> {
        return if (query.isEmpty()) {
            buildList {
                repeat(fakePackages) {
                    add(FakeApp())
                }
            }.toImmutableList().let { flowOf(it) }
        } else {
            flowOf(persistentListOf())
        }
    }
}