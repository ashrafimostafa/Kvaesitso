package ir.mostafa.launcher.plugin

data class Plugin(
    val enabled: Boolean,
    val label: String,
    val description: String? = null,
    val packageName: String,
    val className: String,
    val type: PluginType,
    val authority: String,
)