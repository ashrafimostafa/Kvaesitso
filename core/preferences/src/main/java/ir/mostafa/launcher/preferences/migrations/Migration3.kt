package ir.mostafa.launcher.preferences.migrations

import androidx.datastore.core.DataMigration
import ir.mostafa.launcher.preferences.LauncherSettingsData

class Migration3: DataMigration<LauncherSettingsData> {
    override suspend fun cleanUp() {
    }

    override suspend fun shouldMigrate(currentData: LauncherSettingsData): Boolean {
        return currentData.schemaVersion < 3
    }

    override suspend fun migrate(currentData: LauncherSettingsData): LauncherSettingsData {
        return currentData.copy(
            schemaVersion = 3,
            locationSearchProviders = buildSet {
                if (currentData.locationSearchEnabled) {
                    add("openstreetmaps")
                }
            }
        )
    }
}