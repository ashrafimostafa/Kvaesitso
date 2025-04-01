package ir.mostafa.launcher.plugin.config

data class QueryPluginConfig(
    /**
     * Strategy to store items from this provider in the launcher database
     * @see [StorageStrategy]
     */
    val storageStrategy: StorageStrategy = StorageStrategy.StoreCopy,
)