package ir.mostafa.launcher.ui.ktx

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun Dp.toPixels(): Float {
    return value * LocalDensity.current.density
}