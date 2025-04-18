@file:Suppress("ClassName")

package ir.mostafa.launcher.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import ir.mostafa.launcher.database.daos.PluginDao
import ir.mostafa.launcher.database.daos.ThemeDao
import ir.mostafa.launcher.database.entities.CurrencyEntity
import ir.mostafa.launcher.database.entities.CustomAttributeEntity
import ir.mostafa.launcher.database.entities.ForecastEntity
import ir.mostafa.launcher.database.entities.IconEntity
import ir.mostafa.launcher.database.entities.IconPackEntity
import ir.mostafa.launcher.database.entities.PluginEntity
import ir.mostafa.launcher.database.entities.SavedSearchableEntity
import ir.mostafa.launcher.database.entities.SearchActionEntity
import ir.mostafa.launcher.database.entities.ThemeEntity
import ir.mostafa.launcher.database.entities.WidgetEntity
import ir.mostafa.launcher.database.migrations.Migration_10_11
import ir.mostafa.launcher.database.migrations.Migration_11_12
import ir.mostafa.launcher.database.migrations.Migration_12_13
import ir.mostafa.launcher.database.migrations.Migration_13_14
import ir.mostafa.launcher.database.migrations.Migration_14_15
import ir.mostafa.launcher.database.migrations.Migration_15_16
import ir.mostafa.launcher.database.migrations.Migration_16_17
import ir.mostafa.launcher.database.migrations.Migration_17_18
import ir.mostafa.launcher.database.migrations.Migration_18_19
import ir.mostafa.launcher.database.migrations.Migration_19_20
import ir.mostafa.launcher.database.migrations.Migration_20_21
import ir.mostafa.launcher.database.migrations.Migration_21_22
import ir.mostafa.launcher.database.migrations.Migration_22_23
import ir.mostafa.launcher.database.migrations.Migration_23_24
import ir.mostafa.launcher.database.migrations.Migration_24_25
import ir.mostafa.launcher.database.migrations.Migration_25_26
import ir.mostafa.launcher.database.migrations.Migration_6_7
import ir.mostafa.launcher.database.migrations.Migration_7_8
import ir.mostafa.launcher.database.migrations.Migration_8_9
import ir.mostafa.launcher.database.migrations.Migration_9_10
import ir.mostafa.launcher.ktx.toBytes
import java.util.UUID

@Database(
    entities = [
        ForecastEntity::class,
        SavedSearchableEntity::class,
        CurrencyEntity::class,
        IconEntity::class,
        IconPackEntity::class,
        WidgetEntity::class,
        CustomAttributeEntity::class,
        SearchActionEntity::class,
        ThemeEntity::class,
        PluginEntity::class,
    ], version = 26, exportSchema = true
)
@TypeConverters(ComponentNameConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun weatherDao(): WeatherDao
    abstract fun iconDao(): IconDao

    abstract fun searchableDao(): SearchableDao
    abstract fun widgetDao(): WidgetDao
    abstract fun currencyDao(): CurrencyDao
    abstract fun backupDao(): BackupRestoreDao
    abstract fun customAttrsDao(): CustomAttrsDao

    abstract fun searchActionDao(): SearchActionDao

    abstract fun themeDao(): ThemeDao

    abstract fun pluginDao(): PluginDao

    companion object {
        private var _instance: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            val instance = _instance
                ?: Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "room")
                    //.fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            db.execSQL(
                                "INSERT INTO `SearchAction` (`position`, `type`) VALUES" +
                                        "(0, 'call')," +
                                        "(1, 'message')," +
                                        "(2, 'email')," +
                                        "(3, 'contact')," +
                                        "(4, 'alarm')," +
                                        "(5, 'timer')," +
                                        "(6, 'calendar')," +
                                        "(7, 'website')," +
                                        "(8, 'websearch')"
                            )

                            db.execSQL(
                                "INSERT INTO `SearchAction` (`position`, `type`, `data`, `label`, `color`, `icon`, `customIcon`, `options`) " +
                                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?), (?, ?, ?, ?, ?, ?, ?, ?)",
                                arrayOf(
                                    9,
                                    "url",
                                    context.getString(R.string.default_websearch_2_url),
                                    context.getString(R.string.default_websearch_2_name),
                                    0,
                                    0,
                                    null,
                                    null,
                                    10,
                                    "url",
                                    context.getString(R.string.default_websearch_3_url),
                                    context.getString(R.string.default_websearch_3_name),
                                    0,
                                    0,
                                    null,
                                    null,
                                )
                            )

                            db.execSQL(
                                "INSERT INTO Widget (`type`, `position`, `id`) VALUES " +
                                        "('weather', 0, ?)," +
                                        "('music', 1, ?)," +
                                        "('calendar', 2, ?);",
                                arrayOf(
                                    UUID.randomUUID().toBytes(),
                                    UUID.randomUUID().toBytes(),
                                    UUID.randomUUID().toBytes()
                                )
                            )
                        }
                    })
                    .addMigrations(
                        Migration_6_7(),
                        Migration_7_8(),
                        Migration_8_9(),
                        Migration_9_10(),
                        Migration_10_11(),
                        Migration_11_12(),
                        Migration_12_13(),
                        Migration_13_14(),
                        Migration_14_15(),
                        Migration_15_16(),
                        Migration_16_17(),
                        Migration_17_18(),
                        Migration_18_19(),
                        Migration_19_20(),
                        Migration_20_21(),
                        Migration_21_22(),
                        Migration_22_23(),
                        Migration_23_24(),
                        Migration_24_25(),
                        Migration_25_26(),
                    ).build()
            if (_instance == null) _instance = instance
            return instance
        }
    }
}

