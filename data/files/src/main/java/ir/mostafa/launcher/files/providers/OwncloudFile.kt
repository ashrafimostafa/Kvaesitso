package ir.mostafa.launcher.files.providers

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import ir.mostafa.launcher.files.OwncloudFileSerializer
import ir.mostafa.launcher.files.R
import ir.mostafa.launcher.ktx.tryStartActivity
import ir.mostafa.launcher.search.File
import ir.mostafa.launcher.search.FileMetaType
import ir.mostafa.launcher.search.SearchableSerializer
import kotlinx.collections.immutable.ImmutableMap

internal data class OwncloudFile(
        val fileId: Long,
        override val label: String,
        override val path: String,
        override val mimeType: String,
        override val size: Long,
        override val isDirectory: Boolean,
        val server: String,
        override val metaData: ImmutableMap<FileMetaType, String>,
        override val labelOverride: String? = null,
) : File {

    override fun overrideLabel(label: String): OwncloudFile {
        return this.copy(labelOverride = label)
    }

    override val domain: String = Domain

    override val key: String = "$domain://$server/$fileId"

    override val providerIconRes = R.drawable.ic_badge_owncloud

    private fun getLaunchIntent(): Intent {
        return Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("$server/f/$fileId")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }
    override fun launch(context: Context, options: Bundle?): Boolean {
        return context.tryStartActivity(getLaunchIntent(), options)
    }

    override fun getSerializer(): SearchableSerializer {
        return OwncloudFileSerializer()
    }

    companion object {
        const val Domain = "owncloud"
    }
}