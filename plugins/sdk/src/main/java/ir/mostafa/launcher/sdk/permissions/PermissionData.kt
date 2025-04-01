package ir.mostafa.launcher.sdk.permissions

import kotlinx.serialization.Serializable

@Serializable
internal data class PermissionData(
    val granted: Set<String> = emptySet(),
)