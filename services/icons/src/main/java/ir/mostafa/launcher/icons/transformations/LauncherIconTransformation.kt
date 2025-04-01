package ir.mostafa.launcher.icons.transformations

import ir.mostafa.launcher.icons.LauncherIcon
import ir.mostafa.launcher.icons.StaticLauncherIcon
import ir.mostafa.launcher.icons.TransformableDynamicLauncherIcon

internal interface LauncherIconTransformation {
    suspend fun transform(icon: StaticLauncherIcon): StaticLauncherIcon
}

internal suspend fun LauncherIcon.transform(transformations: Iterable<LauncherIconTransformation>): LauncherIcon {
    if (this is StaticLauncherIcon) {
        var transformedIcon = this
        for (transformation in transformations) {
            transformedIcon = transformation.transform(transformedIcon as StaticLauncherIcon)
        }
        return transformedIcon
    }
    if (this is TransformableDynamicLauncherIcon) {
        this.setTransformations(transformations.toList())
        return this
    }
    return this
}