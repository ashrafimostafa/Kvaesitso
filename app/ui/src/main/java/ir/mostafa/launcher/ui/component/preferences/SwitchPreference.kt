package ir.mostafa.launcher.ui.component.preferences

import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun SwitchPreference(
    title: String,
    icon: ImageVector? = null,
    iconPadding: Boolean = true,
    summary: String? = null,
    value: Boolean,
    onValueChanged: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Preference(
        title = title,
        icon = icon,
        iconPadding = iconPadding,
        summary = summary,
        enabled = enabled,
        onClick = {
            onValueChanged(!value)
        },
        controls = {
            Switch(
                enabled = enabled, checked = value, onCheckedChange = onValueChanged,
            )
        }
    )
}