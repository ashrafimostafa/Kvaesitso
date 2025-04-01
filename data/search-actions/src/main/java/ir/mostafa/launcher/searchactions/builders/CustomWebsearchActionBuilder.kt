package ir.mostafa.launcher.searchactions.builders

import android.content.Context
import android.net.Uri
import ir.mostafa.launcher.searchactions.TextClassificationResult
import ir.mostafa.launcher.searchactions.actions.OpenUrlAction
import ir.mostafa.launcher.searchactions.actions.SearchAction
import ir.mostafa.launcher.searchactions.actions.SearchActionIcon
import java.net.URLEncoder

data class CustomWebsearchActionBuilder(
    override val label: String,
    val urlTemplate: String,
    override val icon: SearchActionIcon = SearchActionIcon.Search,
    override val iconColor: Int = 0,
    override val customIcon: String? = null,
    val encoding: QueryEncoding = QueryEncoding.UrlEncode,
) : CustomizableSearchActionBuilder {

    override val key: String
        get() = "web://$urlTemplate"

    override fun build(context: Context, classifiedQuery: TextClassificationResult): SearchAction {
        val url = urlTemplate.replace("\${1}", encodeQuery(classifiedQuery.text, encoding))
        return OpenUrlAction(
            label = label,
            url = url,
            icon = icon,
            customIcon = customIcon,
            iconColor = iconColor,
        )
    }


    private fun encodeQuery(query: String, encoding: QueryEncoding): String {
        return when (encoding) {
            QueryEncoding.UrlEncode -> Uri.encode(query)
            QueryEncoding.FormData -> URLEncoder.encode(query, "UTF-8")
            QueryEncoding.None -> query
        }
    }

    enum class QueryEncoding {
        UrlEncode,
        FormData,
        None;

        fun toInt(): Int {
            return when (this) {
                UrlEncode -> 0
                FormData -> 1
                None -> 2
            }
        }

        companion object {
            fun fromInt(value: Int?): QueryEncoding {
                return when (value) {
                    1 -> FormData
                    2 -> None
                    else -> UrlEncode
                }
            }
        }
    }
}