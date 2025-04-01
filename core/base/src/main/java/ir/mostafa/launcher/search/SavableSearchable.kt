package ir.mostafa.launcher.search

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import ir.mostafa.launcher.icons.LauncherIcon
import ir.mostafa.launcher.icons.StaticLauncherIcon
import ir.mostafa.launcher.ktx.romanize
import java.text.Collator

interface SavableSearchable : Searchable, Comparable<SavableSearchable>  {
    val key: String

    val label: String
    val labelOverride: String?
        get() = null

    fun overrideLabel(label: String): SavableSearchable

    fun launch(context: Context, options: Bundle?): Boolean

    /**
     * If this is true, tapping the item will open the details popup instead of launching it
     */
    val preferDetailsOverLaunch: Boolean

    fun getPlaceholderIcon(context: Context): StaticLauncherIcon

    suspend fun loadIcon(
        context: Context,
        size: Int,
        themed: Boolean
    ): LauncherIcon? = null

    suspend fun getProviderIcon(context: Context): Drawable? = null

    override fun compareTo(other: SavableSearchable): Int {
        val label1 = labelOverride ?: label
        val label2 = other.labelOverride ?: other.label
        return Collator.getInstance().apply { strength = Collator.SECONDARY }
            .compare(label1.romanize(), label2.romanize())
    }

    val domain: String
    fun getSerializer(): SearchableSerializer

    interface Companion {
        val Domain: String
    }

}