package ir.mostafa.launcher.files

import android.content.Context
import ir.mostafa.launcher.files.providers.GDriveFileProvider
import ir.mostafa.launcher.files.providers.LocalFileProvider
import ir.mostafa.launcher.files.providers.NextcloudFileProvider
import ir.mostafa.launcher.files.providers.OwncloudFileProvider
import ir.mostafa.launcher.files.providers.PluginFileProvider
import ir.mostafa.launcher.nextcloud.NextcloudApiHelper
import ir.mostafa.launcher.owncloud.OwncloudClient
import ir.mostafa.launcher.permissions.PermissionGroup
import ir.mostafa.launcher.permissions.PermissionsManager
import ir.mostafa.launcher.preferences.search.FileSearchSettings
import ir.mostafa.launcher.search.File
import ir.mostafa.launcher.search.SearchableRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

internal class FileRepository(
    private val context: Context,
    private val permissionsManager: PermissionsManager,
    private val settings: FileSearchSettings,
) : SearchableRepository<File> {

    private val nextcloudClient by lazy {
        NextcloudApiHelper(context)
    }
    private val owncloudClient by lazy {
        OwncloudClient(context)
    }

    override fun search(
        query: String,
        allowNetwork: Boolean,
    ): Flow<ImmutableList<File>> {
        if (query.isBlank()) {
            return flowOf(persistentListOf())
        }

        val hasPermission = permissionsManager.hasPermission(PermissionGroup.ExternalStorage)


        return combineTransform(settings.enabledProviders, hasPermission) { providerIds, permission ->
            emit(persistentListOf())
            if (providerIds.isEmpty()) {
                return@combineTransform
            }
            val providers = providerIds.mapNotNull {
                when (it) {
                    "local" -> if (permission) LocalFileProvider(
                        context,
                        permissionsManager
                    ) else null

                    "gdrive" -> GDriveFileProvider(context)
                    "nextcloud" -> NextcloudFileProvider(nextcloudClient)
                    "owncloud" -> OwncloudFileProvider(owncloudClient)
                    else -> PluginFileProvider(context, it)
                }
            }

            supervisorScope {
                val result = MutableStateFlow(persistentListOf<File>())

                for (provider in providers) {
                    launch {
                        val r = provider.search(
                            query,
                            allowNetwork,
                        )
                        result.update {
                            (it + r).toPersistentList()
                        }
                    }
                }
                emitAll(result)
            }

        }
    }
}