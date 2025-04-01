package ir.mostafa.launcher.database

import androidx.room.*
import ir.mostafa.launcher.database.entities.ForecastEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Query("SELECT * FROM ${ForecastEntity.TABLE_NAME} ORDER BY timestamp ASC LIMIT :limit")
    fun getForecasts(limit: Int = 99999): Flow<List<ForecastEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(forecasts: List<ForecastEntity>)

    @Query("DELETE FROM ${ForecastEntity.TABLE_NAME}")
    fun deleteAll()

    @Transaction
    fun replaceAll(forecasts: List<ForecastEntity>) {
        deleteAll()
        insertAll(forecasts)
    }
}