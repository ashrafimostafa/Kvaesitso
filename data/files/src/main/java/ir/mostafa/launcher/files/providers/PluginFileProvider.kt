package ir.mostafa.launcher.files.providers

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import ir.mostafa.launcher.plugin.PluginApi
import ir.mostafa.launcher.plugin.QueryPluginApi
import ir.mostafa.launcher.plugin.config.QueryPluginConfig
import ir.mostafa.launcher.plugin.contracts.FilePluginContract.FileColumns
import ir.mostafa.launcher.plugin.contracts.SearchPluginContract
import ir.mostafa.launcher.plugin.data.set
import ir.mostafa.launcher.plugin.data.withColumns
import ir.mostafa.launcher.search.FileMetaType
import ir.mostafa.launcher.search.UpdateResult
import ir.mostafa.launcher.search.asUpdateResult
import kotlinx.collections.immutable.toPersistentMap

class PluginFileProvider(
    private val context: Context,
    private val pluginAuthority: String,
) : QueryPluginApi<String, PluginFile>(
    context, pluginAuthority
), FileProvider {

    private fun getPluginConfig(): QueryPluginConfig? {
        return PluginApi(pluginAuthority, context.contentResolver).getSearchPluginConfig()
    }

    override fun Cursor.getData(): List<PluginFile>? {
        val config = getPluginConfig()
        val cursor = this

        if (config == null) {
            cursor.close()
            return null
        }

        val results = mutableListOf<PluginFile>()
        val timestamp = System.currentTimeMillis()
        cursor.withColumns(FileColumns) {
            while (cursor.moveToNext()) {
                results.add(
                    PluginFile(
                        id = cursor[FileColumns.Id] ?: continue,
                        path = cursor[FileColumns.Path],
                        mimeType = cursor[FileColumns.MimeType] ?: "application/octet-stream",
                        size = cursor[FileColumns.Size] ?: 0L,
                        metaData = buildMap {
                            cursor[FileColumns.MetaTitle]?.let {
                                put(FileMetaType.Title, it)
                            }
                            cursor[FileColumns.MetaArtist]?.let {
                                put(FileMetaType.Artist, it)
                            }
                            cursor[FileColumns.MetaAlbum]?.let {
                                put(FileMetaType.Album, it)
                            }
                            cursor[FileColumns.MetaDuration]?.let {
                                put(FileMetaType.Duration, DateUtils.formatElapsedTime(it / 1000L))
                            }
                            cursor[FileColumns.MetaYear]?.let {
                                put(FileMetaType.Year, it.toString())
                            }
                            val width = cursor[FileColumns.MetaWidth]
                            val height = cursor[FileColumns.MetaHeight]
                            if (width != null && height != null) {
                                put(FileMetaType.Dimensions, "${width}x${height}")
                            }
                            cursor[FileColumns.MetaLocation]?.let {
                                put(FileMetaType.Location, it)
                            }
                            cursor[FileColumns.MetaAppName]?.let {
                                put(FileMetaType.AppName, it)
                            }
                            cursor[FileColumns.MetaAppPackageName]?.let {
                                put(FileMetaType.AppPackageName, it)
                            }
                            cursor[FileColumns.Owner]?.let {
                                put(FileMetaType.Owner, it)
                            }
                        }.toPersistentMap(),
                        label = cursor[FileColumns.DisplayName] ?: continue,
                        uri = cursor[FileColumns.ContentUri]?.let { Uri.parse(it) } ?: continue,
                        thumbnailUri = cursor[FileColumns.ThumbnailUri]?.let { Uri.parse(it) },
                        storageStrategy = config.storageStrategy,
                        isDirectory = cursor[FileColumns.IsDirectory] ?: false,
                        authority = pluginAuthority,
                        timestamp = timestamp,
                        updatedSelf = {
                            if (it !is PluginFile) UpdateResult.TemporarilyUnavailable()
                            else refresh(it, timestamp).asUpdateResult()
                        }
                    )
                )
            }
        }
        cursor.close()
        return results
    }

    override fun PluginFile.toBundle(): Bundle {
        return Bundle().apply {
            set(FileColumns.Id, id)
            set(FileColumns.Path, path)
            set(FileColumns.ContentUri, uri.toString())
            set(FileColumns.MimeType, mimeType)
            set(FileColumns.Size, size)
            set(FileColumns.MetaTitle, metaData[FileMetaType.Title])
            set(FileColumns.MetaArtist, metaData[FileMetaType.Artist])
            set(FileColumns.MetaAlbum, metaData[FileMetaType.Album])
            set(FileColumns.MetaDuration, metaData[FileMetaType.Duration]?.toLong())
            set(FileColumns.MetaYear, metaData[FileMetaType.Year]?.toInt())
            set(
                FileColumns.MetaWidth,
                metaData[FileMetaType.Dimensions]?.split("x")?.getOrNull(0)?.toInt()
            )
            set(
                FileColumns.MetaHeight,
                metaData[FileMetaType.Dimensions]?.split("x")?.getOrNull(1)?.toInt()
            )
            set(FileColumns.MetaLocation, metaData[FileMetaType.Location])
            set(FileColumns.MetaAppName, metaData[FileMetaType.AppName])
            set(FileColumns.MetaAppPackageName, metaData[FileMetaType.AppPackageName])
            set(FileColumns.Owner, metaData[FileMetaType.Owner])
            set(FileColumns.DisplayName, label)
            set(FileColumns.ThumbnailUri, thumbnailUri?.toString())
            set(FileColumns.IsDirectory, isDirectory)
        }
    }

    override fun Uri.Builder.appendQueryParameters(query: String): Uri.Builder = apply {
        appendQueryParameter(SearchPluginContract.Params.Query, query)
    }
}