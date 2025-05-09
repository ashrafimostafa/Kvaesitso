package ir.mostafa.launcher.preferences.migrations

import androidx.datastore.core.DataMigration
import ir.mostafa.launcher.preferences.ClockWidgetStyle
import ir.mostafa.launcher.preferences.ClockWidgetStyle.Digital1
import ir.mostafa.launcher.preferences.ClockWidgetStyleEnum
import ir.mostafa.launcher.preferences.LauncherSettingsData

class Migration2 : DataMigration<LauncherSettingsData> {
    override suspend fun cleanUp() {
    }

    override suspend fun shouldMigrate(currentData: LauncherSettingsData): Boolean {
        return currentData.schemaVersion < 2
    }

    override suspend fun migrate(currentData: LauncherSettingsData): LauncherSettingsData {
        return currentData.copy(
            schemaVersion = 2,
            clockWidgetUseThemeColor = (currentData._clockWidgetStyle as? Digital1)?.variant == Digital1.Variant.MDY,
            clockWidgetDigital1 = currentData._clockWidgetStyle as? Digital1 ?: Digital1(),
            clockWidgetStyle = when (currentData._clockWidgetStyle) {
                is ClockWidgetStyle.Digital2 -> ClockWidgetStyleEnum.Digital2
                is ClockWidgetStyle.Orbit -> ClockWidgetStyleEnum.Orbit
                is ClockWidgetStyle.Analog -> ClockWidgetStyleEnum.Analog
                is ClockWidgetStyle.Binary -> ClockWidgetStyleEnum.Binary
                is ClockWidgetStyle.Segment -> ClockWidgetStyleEnum.Segment
                is ClockWidgetStyle.Empty -> ClockWidgetStyleEnum.Empty
                else -> ClockWidgetStyleEnum.Digital1
            }
        )
    }
}