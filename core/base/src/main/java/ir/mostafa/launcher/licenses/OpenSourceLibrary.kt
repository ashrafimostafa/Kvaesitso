package ir.mostafa.launcher.licenses

import androidx.annotation.RawRes
import androidx.annotation.StringRes

data class OpenSourceLibrary(
    val name: String,
    val description: String? = null,
    @StringRes val licenseName: Int,
    val copyrightNote: String? = null,
    @RawRes val licenseText: Int,
    val url: String
)