package ir.mostafa.launcher.files.providers

import android.content.Context
import ir.mostafa.launcher.gservices.DriveFileMeta
import ir.mostafa.launcher.gservices.GoogleApiHelper
import ir.mostafa.launcher.search.File
import ir.mostafa.launcher.search.FileMetaType
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableMap

internal class GDriveFileProvider(
    private val context: Context
) : FileProvider {
    override suspend fun search(query: String, allowNetwork: Boolean): List<File> {
        if (query.length < 4 || !allowNetwork) return emptyList()
        val driveFiles = GoogleApiHelper.getInstance(context).queryGDriveFiles(query)
        return driveFiles.map {
            GDriveFile(
                fileId = it.fileId,
                label = it.label,
                size = it.size,
                mimeType = it.mimeType,
                isDirectory = it.isDirectory,
                path = "",
                directoryColor = it.directoryColor,
                viewUri = it.viewUri,
                metaData = getMetadata(it.metadata)
            )
        }
    }

    private fun getMetadata(file: DriveFileMeta): ImmutableMap<FileMetaType, String> {
        val metaData = mutableMapOf<FileMetaType, String>()
        val owners = file.owners
        metaData[FileMetaType.Owner] = owners.joinToString(separator = ", ")
        val width = file.width
        val height = file.height
        if (width != null && height != null) metaData[FileMetaType.Dimensions] = "${width}x${height}"
        return metaData.toImmutableMap()
    }
}