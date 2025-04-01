package ir.mostafa.launcher.files.providers

import ir.mostafa.launcher.owncloud.OwncloudClient
import ir.mostafa.launcher.search.File
import ir.mostafa.launcher.search.FileMetaType
import kotlinx.collections.immutable.persistentMapOf

internal class OwncloudFileProvider(
    private val owncloudClient: OwncloudClient
) : FileProvider {
    override suspend fun search(query: String, allowNetwork: Boolean): List<File> {
        if (query.length < 4 || !allowNetwork) return emptyList()
        val server = owncloudClient.getServer() ?: return emptyList()
        return owncloudClient.files.query(query).map {
            OwncloudFile(
                fileId = it.id,
                label = it.name,
                path = server + it.url,
                mimeType = it.mimeType,
                size = it.size,
                isDirectory = it.isDirectory,
                server = server,
                metaData = it.owner?.let { persistentMapOf(FileMetaType.Owner to it) }
                    ?: persistentMapOf()
            )
        }
    }
}