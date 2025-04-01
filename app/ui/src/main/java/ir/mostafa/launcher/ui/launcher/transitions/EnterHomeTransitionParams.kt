package ir.mostafa.launcher.ui.launcher.transitions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect

@Stable
data class EnterHomeTransitionParams(
    val targetBounds: Rect,
    val icon: (@Composable (animVector: Offset, progress: () -> Float) -> Unit)? = null
)