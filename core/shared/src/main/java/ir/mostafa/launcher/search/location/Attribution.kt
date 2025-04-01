package ir.mostafa.launcher.search.location

import android.net.Uri
import ir.mostafa.launcher.serialization.UriSerializer
import kotlinx.serialization.Serializable

@Serializable
data class Attribution(
    val text: String? = null,
    @Serializable(with = UriSerializer::class)
    val iconUrl: Uri? = null,
    val url: String? = null,
)