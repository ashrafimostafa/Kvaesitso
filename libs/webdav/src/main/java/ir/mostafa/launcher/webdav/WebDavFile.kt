package ir.mostafa.launcher.webdav

data class WebDavFile(
        val name: String,
        val id: Long,
        val url: String,
        val isDirectory: Boolean,
        val mimeType: String,
        val size: Long,
        val owner: String?
)