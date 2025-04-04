package ir.mostafa.launcher.wikipedia

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import ir.mostafa.launcher.icons.ColorLayer
import ir.mostafa.launcher.icons.StaticLauncherIcon
import ir.mostafa.launcher.icons.TintedIconLayer
import ir.mostafa.launcher.ktx.tryStartActivity
import ir.mostafa.launcher.search.Article
import ir.mostafa.launcher.search.SearchableSerializer

internal data class Wikipedia(
    override val label: String,
    val id: Long,
    override val text: String,
    override val imageUrl: String?,
    override val sourceUrl: String,
    override val sourceName: String,
    val wikipediaUrl: String,
    override val labelOverride: String? = null,
) : Article {

    override val domain: String = Domain

    override fun overrideLabel(label: String): Wikipedia {
        return this.copy(labelOverride = label)
    }

    override val key = "$domain://$wikipediaUrl:$id"

    override fun getPlaceholderIcon(context: Context): StaticLauncherIcon {
        return StaticLauncherIcon(
            foregroundLayer = TintedIconLayer(
                icon = ContextCompat.getDrawable(context, R.drawable.ic_wikipedia)!!,
                scale = 1f,
                color = 0xFFC1C2C4.toInt(),
            ),
            backgroundLayer = ColorLayer(0xFFC1C2C4.toInt())
        )
    }

    private fun getLaunchIntent(): Intent {
        return Intent(Intent.ACTION_VIEW, Uri.parse(sourceUrl))
    }

    override fun launch(context: Context, options: Bundle?): Boolean {
        return context.tryStartActivity(getLaunchIntent(), options)
    }

    override val canShare: Boolean = true
    override fun share(context: Context) {
        val text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.putExtra(
            Intent.EXTRA_TEXT, "${label}\n\n" +
                    "${text.substring(0, 200.coerceAtMost(text.length))}…\n\n" +
                    sourceUrl
        )
        shareIntent.type = "text/plain"
        context.startActivity(Intent.createChooser(shareIntent, null))
    }

    override fun getSerializer(): SearchableSerializer {
        return WikipediaSerializer()
    }

    companion object {
        const val Domain = "wikipedia"
    }
}