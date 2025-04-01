package ir.mostafa.launcher.icons

import ir.mostafa.launcher.icons.transformations.LauncherIconTransformation

internal interface TransformableDynamicLauncherIcon {
    fun setTransformations(transformations: List<LauncherIconTransformation>)
}