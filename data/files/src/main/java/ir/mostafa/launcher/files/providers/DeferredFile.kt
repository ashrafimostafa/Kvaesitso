package ir.mostafa.launcher.files.providers

import ir.mostafa.launcher.search.File
import ir.mostafa.launcher.search.SavableSearchable
import ir.mostafa.launcher.search.UpdatableSearchable
import ir.mostafa.launcher.search.UpdateResult

class DeferredFile(
    cachedFile: File,
    override val timestamp: Long,
    override var updatedSelf: (suspend (SavableSearchable) -> UpdateResult<File>)? = null,
) : File by cachedFile, UpdatableSearchable<File>