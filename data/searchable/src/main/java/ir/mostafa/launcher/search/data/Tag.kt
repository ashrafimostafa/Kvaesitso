package ir.mostafa.launcher.search.data

import android.content.Context
import android.os.Bundle
import ir.mostafa.launcher.icons.ColorLayer
import ir.mostafa.launcher.icons.StaticLauncherIcon
import ir.mostafa.launcher.icons.TextLayer
import ir.mostafa.launcher.search.SavableSearchable
import ir.mostafa.launcher.search.SearchableSerializer
import ir.mostafa.launcher.searchable.TagSerializer

data class Tag(
    val tag: String,
    override val labelOverride: String? = null
): SavableSearchable {

    override val domain: String = Domain

    override val key: String = "$domain://$tag"
    override val label: String = tag

    override val preferDetailsOverLaunch: Boolean = true

    override fun launch(context: Context, options: Bundle?): Boolean {
        return false
    }
    override fun overrideLabel(label: String): SavableSearchable {
        return this.copy(labelOverride = label)
    }

    override fun getPlaceholderIcon(context: Context): StaticLauncherIcon {
        return StaticLauncherIcon(
            foregroundLayer = TextLayer("#"),
            backgroundLayer = ColorLayer()
        )
    }

    override fun getSerializer(): SearchableSerializer {
        return TagSerializer()
    }

    companion object {
        const val Domain = "tag"
    }
}