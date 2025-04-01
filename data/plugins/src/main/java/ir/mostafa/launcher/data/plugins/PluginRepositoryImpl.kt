package ir.mostafa.launcher.data.plugins

import ir.mostafa.launcher.database.daos.PluginDao
import ir.mostafa.launcher.plugin.Plugin
import ir.mostafa.launcher.plugin.PluginRepository
import ir.mostafa.launcher.plugin.PluginType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

internal class PluginRepositoryImpl(
    private val dao: PluginDao,
) : PluginRepository {

    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    override fun findMany(
        type: PluginType?,
        enabled: Boolean?,
        packageName: String?
    ): Flow<List<Plugin>> {
        return dao.findMany(
            type = type?.name,
            enabled = enabled,
            packageName = packageName,
        ).map {
            it.mapNotNull { Plugin(it) }
        }
    }

    override fun get(authority: String): Flow<Plugin?> {
        return dao.get(authority).map { it?.let { Plugin(it) } }
    }

    override fun insertMany(plugins: List<Plugin>): Job {
        return scope.launch {
            dao.insertMany(plugins.map { PluginEntity(it) })
        }
    }

    override fun insert(plugin: Plugin): Job {
        return scope.launch {
            dao.insert(PluginEntity(plugin))
        }
    }

    override fun update(plugin: Plugin): Job {
        return scope.launch {
            dao.update(PluginEntity(plugin))
        }
    }

    override fun updateMany(plugins: List<Plugin>): Job {
        return scope.launch {
            dao.updateMany(
                plugins.map { PluginEntity(it) }
            )
        }
    }

    override fun deleteMany(): Job {
        return scope.launch {
            dao.deleteMany()
        }
    }
}