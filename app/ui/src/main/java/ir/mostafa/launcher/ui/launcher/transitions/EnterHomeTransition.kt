package ir.mostafa.launcher.ui.launcher.transitions

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect

data class EnterHomeTransition(
    val startBounds: Rect? = null,
    val targetBounds: Rect? = null,
    val icon: (@Composable (animVector: Offset, progress: () -> Float) -> Unit)? = null
)