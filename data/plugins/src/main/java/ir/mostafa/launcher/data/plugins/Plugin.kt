package ir.mostafa.launcher.data.plugins

import ir.mostafa.launcher.database.entities.PluginEntity
import ir.mostafa.launcher.plugin.Plugin
import ir.mostafa.launcher.plugin.PluginType

internal fun Plugin(entity: PluginEntity): Plugin? {
    return Plugin(
        enabled = entity.enabled,
        label = entity.label,
        description = entity.description,
        packageName = entity.packageName,
        className = entity.className,
        type = try {
            PluginType.valueOf(entity.type)
        } catch (e: IllegalArgumentException) {
            return null
        },
        authority = entity.authority,
    )
}

internal fun PluginEntity(plugin: Plugin): PluginEntity {
    return PluginEntity(
        enabled = plugin.enabled,
        label = plugin.label,
        description = plugin.description,
        settingsActivity = null,
        packageName = plugin.packageName,
        className = plugin.className,
        type = plugin.type.name,
        authority = plugin.authority,
    )
}