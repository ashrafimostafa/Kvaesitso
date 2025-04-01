package ir.mostafa.launcher.files.providers

import ir.mostafa.launcher.search.File

internal interface FileProvider {
    suspend fun search(query: String, allowNetwork: Boolean): List<File>
}