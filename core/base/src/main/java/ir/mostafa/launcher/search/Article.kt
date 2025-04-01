package ir.mostafa.launcher.search

import android.content.Context

interface Article: SavableSearchable {

    val text: String
    val imageUrl: String?
    val sourceUrl: String
    val sourceName: String

    val canShare: Boolean
    fun share(context: Context) {}

    override val preferDetailsOverLaunch: Boolean
        get() = false
}